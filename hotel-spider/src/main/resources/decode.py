import re

def i(a,c,d) :
    jisuan="".join(a)
    g = 0
    index = 0
    i = len(jisuan)
    j = ""
    k = 0
    l =0
    m=0
    while ( i > index):
        l = ord(jisuan[index])
        if(256 > l ):
            l=(d[l])
        else:
            l=-1
        g = (g << 6) + l
        k += 6
        while (k >= 8 ):
            k -= 8
            m = g >> k
            j += c[m]
            g ^= m << k
        index+=1

    return j

def decode(data):
    data=data.replace("*!agf", "=")
    data=data.replace("&a^f", "b")

    b = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'
    c = ""
    d = [-1] * 256
    e = [-1] * 256
    f = 0
    h = ""
    for num in range(0,256):
        f=num
        h = chr(f)
        c+=str(h)
        e[f] = f
        try:
            d[f] = b.index(str(h))
        except:
            d[f] =-1
    data="".join(re.findall('[a-zA-Z0-9]+', data, re.S))
    return (i(data,c,d))