# step268-2020

To run the gtfs-validator locally with Gradle:

1. Ensure Gradle is installed
2. From the root directory you can run:
   gradle gtfs-validator:main:run --args="--input [path to zip input file] -o [path to directory to store output]
   --feed_name [name in form "ab-abc"] --threads [num threads]"
    
