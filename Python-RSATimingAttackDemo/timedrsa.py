Integer = int

def multMod(a, b, n):
    """
    Computes a*b mod n using "shift and add" algorithm.
    """
    result = Integer(0)
    timer = 0
    bits = bin(b)[2:]
    bitlen = len(bits)
    
    for i in range(bitlen):
        result += result
        timer += 1
        if result >= n:
            result -= n
            timer += 1
        if bits[i] == '1':
            result += a
            timer += 1
            if result >= n:
                result -= n
                timer += 1
    return result, timer
