This Application reads a File called "domains.txt" containing a List of Domains, establishes a connection to every Server and gets the Expiration Date of the certificate. After that a Table( using the Table class) containing the Hostname, the Remaining Days, the Expiration Date and the failed attempts will be outputted. This table will be sorted in Order from most to least remaining days and will seperate the Domains that will expire in under 22 Days.
