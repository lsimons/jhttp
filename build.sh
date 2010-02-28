#!/bin/sh

set -e
if [[ "$1" == "-v" ]]; then
    set -x
    shift
fi

# settings
basedir=`dirname $0`
basedir=$(cd "$basedir" && pwd)
currdir=`pwd`

project="jhttp"
srcdir="$basedir/src"
testdir="$basedir/test"
testrptdir="$currdir/test-output"
libdir="$basedir/lib"
distdir="$currdir/build"
classdir="$distdir/classes"
testclassdir="$distdir/test-classes"
apidocdir="$distdir/doc/api"
coberturadir="$basedir/cobertura-1.9.3"
instrumentedclassdir="$distdir/instrumented-classes"
coberturarptdir="$distdir/doc/coverage"

read version < "$basedir/VERSION" || : echo ignored
cmd=${1:-build}

usage() {
    echo "./build.sh [-v] [help|clean|compile|test|integration-test|jar|javadoc|dist|build|coverage]"
    echo "  (when building from a distribution, you must download and extract the binary"
    echo "  distribution of cobertura into ./cobertura-1.9.3/ to enable the coverage command)"
}

# validate
cmdok=0
for okcmd in help clean compile test integration-test jar javadoc dist build coverage; do
    if [[ "$cmd" == "$okcmd" ]]; then
        cmdok=1
        break
    fi
done
[[ $cmdok -ne 1 ]] && usage && exit 1
[[ "$cmd" == "help" ]] && usage && exit 0
if [[ "$cmd" == "clean" ]]; then
    rm -rf "$distdir"
    exit 0
fi

echo "preparing..."
CP="$CLASSPATH"
for l in `find $libdir -type f -name '*.jar' | sort -r`; do
    CP="$l:$CP"
done
CP="$classdir:$CP"



echo "compiling..."
rm -rf "$classdir"
mkdir -p "$classdir"
cd "$srcdir"
set +e
javac -nowarn -Xlint:-deprecation -source 1.5 -target 1.5 \
        -d "$classdir" \
        -cp "$CP" \
        `find . -name '*.java'`
if [[ $? -ne 0 ]]; then
    echo "BUILD FAILED! (compile error)"
    exit 1
fi
set -e

# resources
#for d in `find . -type d`; do
#    mkdir -p "$classdir/$dir"
#done
#
#cp -r `find . -type f -not -name '*.java'` \
#        "$classdir"

[[ "$cmd" == "compile" ]] && echo "...done" && exit 0

if [[ "$cmd" == "coverage" || "$cmd" == "dist" ]]; then
    set +e
    for l in `find $coberturadir -type f -name '*.jar' | sort -r`; do
        CP="$CP:$l"
    done
    set -e
    echo "instrumenting..."
    rm -rf "$instrumentedclassdir"
    mkdir -p "$instrumentedclassdir"
    set +e
    java \
        -cp "$CP" \
        net.sourceforge.cobertura.instrument.Main \
        --destination "$instrumentedclassdir" \
        --datafile "$instrumentedclassdir/cobertura.ser" \
        "$classdir"
    if [[ $? -ne 0 ]]; then
        echo "INSTRUMENTATION FAILED! (ignoring)"
    fi
    set -e
fi


echo "compiling tests..."
rm -rf "$testclassdir"
mkdir -p "$testclassdir"
cd "$testdir"
TCP="$testclassdir:$CP"

set +e
javac -nowarn -Xlint:-deprecation -source 1.5 -target 1.5 \
        -d "$testclassdir" \
        -cp "$TCP" \
        `find . -type f -name '*.java'`
if [[ $? -ne 0 ]]; then
    echo "BUILD FAILED! (compile error)"
    exit 1
fi
set -e

rsync -a --exclude="*.java" ./ $testclassdir/


echo "testing..."

props=""
if [[ "$cmd" == "coverage" || "$cmd" == "dist" ]]; then
    TCP="$instrumentedclassdir:$TCP"
    props="-Dnet.sourceforge.cobertura.datafile=$instrumentedclassdir/cobertura.ser"
fi

cd "$distdir"
testngxml="$testdir/testng-checkin.xml"
[[ "$cmd" == "test" \
    || $cmd == "dist" \
    || $cmd == "coverage" ]] && testngxml="$testngxml $testdir/testng-func.xml"
[[ "$cmd" == "integration-test" ]] && testngxml="$testdir/testng-integration.xml"
[[ "$cmd" == "coverage" ]] && testngxml="$testngxml $testdir/testng-integration.xml"

rm -rf "$testrptdir"
set +e
java -ea -cp "$TCP" $props \
        org.testng.TestNG \
        -sourcedir "$testdir" \
        $testngxml
if [[ $? -ne 0 ]]; then
    echo "  test report is ${currdir}/test-output/index.html"
    echo "BUILD FAILED! (test failure)"
    exit 1
fi
set -e
echo "  test report is ${currdir}/build/test-output/index.html"
echo "...tests ok"

if [[ "$cmd" == "coverage" || "$cmd" == "dist" ]]; then
    echo "generating test report..."
    rm -rf "$coberturarptdir"
    mkdir -p "$coberturarptdir"
    set +e
    java \
        -cp "$CP" \
        net.sourceforge.cobertura.reporting.Main \
        --destination "$coberturarptdir" \
        --datafile "$instrumentedclassdir/cobertura.ser" \
        "$srcdir"
    if [[ $? -ne 0 ]]; then
        echo "COVERAGE REPORTING FAILED! (ignoring)"
    else
        echo "  coverage report is ${currdir}/build/doc/coverage/index.html"
    fi
    set -e
fi

[[ "$cmd" == "test" \
    || "$cmd" == "integration-test" \
    || "$cmd" == "build" \
    || "$cmd" == "coverage" ]] && echo "...done" && exit 0

echo "jarring..."
cd "$classdir"
mkdir -p "META-INF"
cp "$basedir/JAR_LICENSE.txt" "META-INF/LICENSE.txt"

mkdir -p "$distdir"
jar cf "$distdir/$project-$version.jar" *

[[ "$cmd" == "jar" ]] && echo "...done" && exit 0

echo "generating javadoc..."
javadoc \
    -sourcepath "$srcdir" \
    -d "$apidocdir" \
    -windowtitle "$project $version API" \
    -doctitle "$project $version API" \
    -link "http://java.sun.com/j2se/1.5.0/docs/api/" \
    -subpackages net

echo "  javadocs in $apidocdir"
[[ "$cmd" == "javadoc" ]] && echo "...done" && exit 0


echo "packaging..."
rm -rf "$distdir/$project-$version"
mkdir -p "$distdir/$project-$version"
cp "$distdir/$project-$version.jar" "$distdir/$project-$version"
cd "$basedir"
cp \
    *.txt \
    *.sh \
    VERSION \
    "$distdir/$project-$version"
cp -r \
    src \
    lib \
    test \
    "$distdir/$project-$version"
cp -r "$distdir/doc" "$distdir/$project-$version"

cd "$distdir/$project-$version/src"
mkdir -p META-INF
cp "$basedir/JAR_LICENSE.txt" "META-INF/LICENSE.txt"
jar cf "../$project-$version-src.jar" *
rm -rf META-INF

cd "$distdir"
#jar cf "$project-$version.zip" "$project-$version"
tar czf "$project-$version.tar.gz" "$project-$version"
echo "  distribution is $distdir/$project-$version.tar.gz"

echo "...done"