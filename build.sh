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

read version < "$basedir/VERSION" || : echo ignored
cmd=${1:-build}

# validate
usage() {
    echo "./build.sh [-v] [help|clean|compile|test|integration-test|jar|javadoc|dist|build]"
}

cmdok=0
for okcmd in help clean compile test integration-test jar javadoc dist build; do
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
javac -nowarn -Xlint:-deprecation -source 1.5 -target 1.5 \
        -d "$classdir" \
        -cp "$CP" \
        `find . -name '*.java'` || (echo "BUILD FAILED! (compile error)"; exit 1)

# resources
#for d in `find . -type d`; do
#    mkdir -p "$classdir/$dir"
#done
#
#cp -r `find . -type f -not -name '*.java'` \
#        "$classdir"

[[ "$cmd" == "compile" ]] && echo "...done" && exit 0



echo "compiling tests..."
rm -rf "$testclassdir"
mkdir -p "$testclassdir"
cd "$testdir"
TCP="$testclassdir:$CP"

javac -nowarn -Xlint:-deprecation -source 1.5 -target 1.5 \
        -d "$testclassdir" \
        -cp "$TCP" \
        `find . -type f -name '*.java'` || (echo "BUILD FAILED! (test compile error)"; exit 1)



echo "testing..."
cd "$distdir"
testngxml="$testdir/testng-checkin.xml"
[[ "$cmd" == "test" ]] && testngxml="$testngxml $testdir/testng-func.xml"
[[ "$cmd" == "integration-test" ]] && testngxml="$testdir/testng-integration.xml"

rm -rf "$testrptdir"
set +e
java -ea -cp "$TCP" \
        org.testng.TestNG \
        -sourcedir "$testdir" \
        $testngxml
if [[ $? -ne 0 ]]; then
    echo "  test report is ${currdir}/test-output/index.html"
    echo "BUILD FAILED! (test failure)"
    exit 1
fi
set -e
echo "  test report is ${currdir}/test-output/index.html"
echo "...tests ok"

[[ "$cmd" == "test" \
    || "$cmd" == "integration-test" \
    || "$cmd" == "build" ]] && echo "...done" && exit 0



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