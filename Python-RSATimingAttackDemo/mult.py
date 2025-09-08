#!/usr/bin/python3

from timedrsa import Integer
from timedrsa import multMod

a = Integer("34847592847")
b = Integer("17324759283")
m = Integer("99348759348")

result, timing = multMod(a,b,m)

print("multmod(%s, %s, %s)=%s in %s Zeiteinheiten" %  (a,b,m,result,timing))


