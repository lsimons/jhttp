jHttp is a java http client library.

Get:
    git clone git://github.com/lsimons/jhttp.git

Build:
    ./build.sh
    ./build.sh integration-test
    ./build.sh help

Use:
    API entrypoint is net.jhttp.Http:

        import net.jhttp.Http;

        public class Sample {
          public static void main(String[] args) {
            String body =
            
                Http.client().GET("http://www.example.org/").responseBody();
            
            
            System.out.println(body);
          }
        }

    See the javadocs in doc/api for details.

At a glance:
    * MIT license (in LICENSE.txt).
    * requires java 1.5 or later.
    * no runtime dependencies.
    * 100% test coverage.
    * extensive integration tests.
    * complete and accurate javadocs.
    * detailed release notes (in RELEASE_NOTES.txt).

Contribute:
    * Clone the master repo.

        git clone git://github.com/lsimons/jhttp.git

    * Make changes.
    * Add yourself to AUTHORS.
    * Push your changes back to your own github account.
    * Send a pull request to lsimons.
    * Read http://help.github.com/forking/ for more help.

Support:
    Sorry, none available. Read the docs, read the source code or try google.
