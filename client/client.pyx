from libc.stdlib cimport malloc
from libc.string cimport strcpy, strlen
import socket
import random
import json

connection_sockets = []
primary_server = -1

cdef public int kv739_init(char **servers, int num_servers):
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
        
    if(len(connection_sockets) < num_servers):
        print "Could not connect to all servers in init"
        cleanup(connection_sockets)
        return -1

    # Pick one server to communicate to as a primary. We fallback to secondaries only in event of failure
    primary_server = random.randint(0, len(connection_sockets)-1)
    print("Assigning primary server")
    return 0

cdef public int kv739_shutdown():
    cleanup(connection_sockets)
    return -1

cdef public int kv739_get(char * key, char * value):
    status, val = getValueForKey(key, primary_server)
    if status == 1:
        strcpy(value, val)
    return status

cdef public int kv739_put(char * key, char * value, char * old_value):
    status, oldval = setValueForKey(key,value, primary_server)
    if status == 1:
        strcpy(old_value, oldval)
    return status

def cleanup(connection_sockets):
    for i in range(0, len(connection_sockets)):
        connection_sockets[i].close()
    primary_server = -1
    connection_sockets = []

def getValueForKey(key, primary_server):
    data = {}
    data['operation'] = 'GET'
    data['key'] = key
    json_data = json.dumps(data)
    # Attempt to send request to primary server

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
        value = ""
        response_status = -1
        response = connection_sockets[primary_server].recv(2048)
        json_response = json.loads(response)
        if json_response["status"] == "success":
            value = json_response["value"]
            response_status = 1
        return response_status, value
    except socket.error, msg:
        print "Exception. Returning empty"
        return -1, ""

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
        response = connection_sockets[primary_server].recv(2048)
        json_response = json.loads(response)
        old_value = ""
        response_status = -1
        if json_response["status"] == "success":
            old_value = json_response["value"]
            response_status = 1
        return response_status, old_value
    except socket.error, msg:
        print "Exception. Returning empty"
        return -1, ""
