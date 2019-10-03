# Compile the client shared library and tests
> bash compile_client_and_tests.sh -s KVSTORE_SIZE -t SLEEP_TIME

# Execute tests
The following tests can be executed:
## Correctness test for single client
This tests inserts *KVSTORE_SIZE* keys into the *SS-KVSTORE* using a single client
and tests if the client is able to read them back. Subsequently, the test
client updates all the keys and ensures that the expected old value if returned.
> ./correctness_test_single_client *server1* *server2* *server3*
## Correctness test for single client with failure of single server
This test inserts *KVSTORE_SIZE* keys and values into the *SS-KVSTORE* using
a single client before killing one of the servers. The client then reads the
values of all keys to see if they're as expected. The killed server is then
restarted and the values for all keys are read again to see if they're still
returned as expected.
> ./correctness_test_single_client_failures *server1* *server2* *server3* *PID1*
## Correctness test for single client with failure of two servers
This test is similar to the previous test, with the difference being that two
servers are killed and restarted.
> ./correctness_test_single_client_failures_2servers *server1* *server2* *server3* *PID1* *PID2*
## Correctness test for two clients without failure
The tests starts by writing *KVSTORE_SIZE* keys and values into *SS-KVSTORE*.
Then, two clients connect to two distinct servers in *SS-KVSTORE* and execute in locksteps, one of them updating the value for
a particular key and the other checking if it can see the update.
> ./correctness_test_two_clients
## Correctness test for two clients with failure
Two clients connect to two distinct servers in *SS-KVSTORE*. One of them
writes *KVSTORE_SIZE* keys and values to *SS-KVSTORE*, and then kills one of
the servers in *SS-KVSTORE*. Then, the other client reads all keys and values
checking if they're as expected.
> ./correctness_test_two_clients_failure

**Note:** *serveri* is the *hostname:port* at which the server is hosted and
*PIDi* is the PID of the server to kill.
