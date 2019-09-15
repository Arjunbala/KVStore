from libc.stdlib cimport malloc
from libc.string cimport strcpy, strlen
import socket
import random
import json

connection_sockets = []
primary_server = -1 # TODO: Handle failover

cdef public int kv739_init(char **servers, int num_servers):
    # TODO: Implement
    cdef char **srvs = servers
    cdef int i

    # Establish socket connection to each of the servers
    for i in range (0,num_servers):
        print ("Establishing connection to " + srvs[i])
        server = srvs[i]
        connection_sockets.append(socket.socket())
        try:
            connection_sockets[i].connect((server.split(":")[0], int(server.split(":")[1])))
        except socket.error, exc:
            print "Caught exception socket.error : %s" % exc
            # TODO: cleanup
            return -1
    
    # Pick one server to communicate to as a primary. We fallback to secondaries only in event of failure
    primary_server = random.randint(0, len(connection_sockets)-1)
    return 0

cdef public int kv739_shutdown():
    # TODO: Implement. Close all connections and cleanup
    return -1

cdef public int kv739_get(char * key, char * value):
    # TODO: Implement
    val = getValueForKey(key)
    strcpy(value, val)
    return -1

cdef public int kv739_put(char * key, char * value, char * old_value):
    #TODO: Implement
    oldval = setValueForKey(key,value)
    strcpy(old_value, oldval)
    return -1

def getValueForKey(key):
    data = {}
    data['operation'] = 'GET'
    data['key'] = key
    json_data = json.dumps(data)
    # TODO: Implement
    # Attempt to send request to primary server

    # TODO: Error handling and failover
    print('Sending...')
    connection_sockets[primary_server].sendall(json_data + "\n") 
    print('Sent...')
    # connection_sockets[primary_server].send('')
    valueFromServer = connection_sockets[primary_server].recv(2048)
    return valueFromServer

def setValueForKey(key,value):
    data = {}
    data['operation'] = 'PUT'
    data['key'] = key
    data['value'] = value
    json_data = json.dumps(data)
    connection_sockets[primary_server].sendall(json_data + "\n")
    valueFromServer = connection_sockets[primary_server].recv(2048)
    return valueFromServer
