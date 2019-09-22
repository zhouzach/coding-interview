
https://dzone.com/articles/tips-and-best-practices-to-take-advantage-of-spark
When persisting and compressing CSV and JSON files, make sure they are splittable, 
give high speeds, and yield reasonable compression. ZIP compression is not splittable, 
whereas Snappy is splittable; Snappy also gives reasonable compression with high speed. 
When reading CSV and JSON files, you will get better performance by specifying the schema instead of using inference;
 specifying the schema reduces errors for data types and is recommended for production code.