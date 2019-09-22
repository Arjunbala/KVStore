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
        try:
            sock = socket.socket()
            sock.connect((server.split(":")[0], int(server.split(":")[1])))
            connection_sockets.append(sock)
        except socket.error, exc:
            print "Caught exception socket.error : %s" % exc
        
    if(len(connection_sockets) == 0):
        return -1

    # Pick one server to communicate to as a primary. We fallback to secondaries only in event of failure
    primary_server = random.randint(0, len(connection_sockets)-1)
    print("Assigning primary server")
    return 0

cdef public int kv739_shutdown():
    # TODO: Implement. Close all connections and cleanup
    return -1

cdef public int kv739_get(char * key, char * value):
    # TODO: Implement
    val = getValueForKey(key, primary_server)
    strcpy(value, val)
    return -1

cdef public int kv739_put(char * key, char * value, char * old_value):
    #TODO: Implement
    oldval = setValueForKey(key,value, primary_server)
    strcpy(old_value, oldval)
    return -1

def getValueForKey(key, primary_server):
    data = {}
    data['operation'] = 'GET'
    data['key'] = key
    json_data = json.dumps(data)
    # TODO: Implement
    # Attempt to send request to primary server

    # TODO: Error handling and failover
    print('Sending...')
    failovers = 0
    while failovers < len(connection_sockets):
        try:
            connection_sockets[primary_server].sendall(json_data + "\n")
            break
        except socket.error, msg:
            print "Couldnt connect with the socket-server: initiating fail over" % msg
            primary_server = (primary_server + 1) % len(connection_sockets)
            print "New primary server is " % primary_server
            failovers = failovers + 1
    print('Sent...')
    # connection_sockets[primary_server].send('')
    try:
        valueFromServer = connection_sockets[primary_server].recv(2048)
        return valueFromServer
    except socket.error, msg:
        print "Exception. Returning empty"
        return ""

def setValueForKey(key, value, primary_server):
    data = {}
    data['operation'] = 'PUT'
    data['key'] = key
    data['value'] = value
    json_data = json.dumps(data)
    failovers = 0
    while failovers < len(connection_sockets):
        try:
            connection_sockets[primary_server].sendall(json_data + "\n")
            break
        except socket.error, msg:
            print "Couldnt connect with the socket-server: initiating fail over" % msg
            primary_server = (primary_server + 1) % len(connection_sockets)
            print "New primary server is " % primary_server
            failovers = failovers + 1
    try:        
        valueFromServer = connection_sockets[primary_server].recv(2048)
        return valueFromServer
    except socket.error, msg:
        print "Exception. Returning empty"
        return ""
