from ctypes import *
import json

dll = CDLL("target/libsiemplify.so")
isolate = c_void_p()
isolatethread = c_void_p()
dll.graal_create_isolate(None, byref(isolate), byref(isolatethread))
dll.do_the_thing_wrapper.restype = c_char_p

def runit():
    data = json.dumps({"hello":"python"})
    encoded = data.encode('utf8')
    result = dll.do_the_thing_wrapper(isolatethread, c_char_p(encoded), len(encoded))
    return json.loads(result.decode('utf8'))
