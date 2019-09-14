from libc.stdlib cimport malloc
from libc.string cimport strcpy, strlen

cdef public int kv739_init(char **servers, int num_servers):
    # TODO: Implement
    cdef char **srvs = servers
    cdef int i
    for i in range (0,num_servers):
        print (srvs[i])
    return -1

cdef public int kv739_shutdown():
    # TODO: Implement
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
    # TODO: Implement
    return "Arjun"

def setValueForKey(key,value):
    return "Danish"
