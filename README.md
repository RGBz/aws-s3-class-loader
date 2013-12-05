aws-s3-class-loader
===================

A Java ClassLoader implementation that yanks classes directly from an Amazon Web Services S3 bucket.

Huh?
----

This is a special kind of ClassLoader that you can point at an S3 bucket on AWS. This means that you can store Java .class files
in a central location and only have them loaded when needed.

The class files are expected to be stored with their full class names as the bucket key.
So for example, the following class's key is expected to be "st.engineer.Dog":  

    package st.engineer;
    
    class Dog {
        
        public String bark() {
            return "woof";
        }
    }

Why?
----

Why not?

Try it out
----------

First you need to add an AwsCredentials.properties file to your classpath (See here for details http://aws.amazon.com/articles/3586).

Then to run the test classes, run this on the command line:

    ./gradlew test
    
If you want to use the ClassLoader in your own project, just create a JAR to reference by running:

    ./gradlew jar
    
