#include <Python.h>
#include "client.h"
#include <stdio.h>
#include <string.h>

#define SERVER_LOC_SIZE 50
#define NUM_SERVERS 1
#define MAX_VAL_SIZE 2048

int main()
{
    Py_Initialize();
    initclient();
    
    // Test code starts here
    char **servers;
    servers = (char**)malloc(NUM_SERVERS*sizeof(char*));

    for(int i=0;i<NUM_SERVERS;i++) {
	servers[i] = (char*)malloc(SERVER_LOC_SIZE*sizeof(char));
	strcpy(servers[i], "localhost:9100");
    }

    int ret = kv739_init(servers, NUM_SERVERS);
    printf("Kv_init %d\n", ret);

    char *val;

    val = (char*) malloc(MAX_VAL_SIZE * sizeof(char));
    ret = kv739_get("abc", val);
    printf("%s\n", val);

    ret = kv739_put("abc", "def", val);
    printf("%s\n", val);

    Py_Finalize();
    return 0;
}
