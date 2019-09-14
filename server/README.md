To compile code
mvn compile

To start key value server at a particular port (say 9100 in this example)
mvn exec:java -Dexec.mainClass="com.cs739.kvstore.KeyValueServer" -Dexec.args="9100"
