/* Generated by Cython 0.29.13 */

#ifndef __PYX_HAVE__client
#define __PYX_HAVE__client


#ifndef __PYX_HAVE_API__client

#ifndef __PYX_EXTERN_C
  #ifdef __cplusplus
    #define __PYX_EXTERN_C extern "C"
  #else
    #define __PYX_EXTERN_C extern
  #endif
#endif

#ifndef DL_IMPORT
  #define DL_IMPORT(_T) _T
#endif

__PYX_EXTERN_C int kv739_init(char **, int);
__PYX_EXTERN_C int kv739_shutdown(void);
__PYX_EXTERN_C int kv739_get(char *, char *);
__PYX_EXTERN_C int kv739_put(char *, char *, char *);

#endif /* !__PYX_HAVE_API__client */

/* WARNING: the interface of the module init function changed in CPython 3.5. */
/* It now returns a PyModuleDef instance instead of a PyModule instance. */

#if PY_MAJOR_VERSION < 3
PyMODINIT_FUNC initclient(void);
#else
PyMODINIT_FUNC PyInit_client(void);
#endif

#endif /* !__PYX_HAVE__client */
