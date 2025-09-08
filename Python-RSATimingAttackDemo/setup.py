#!/usr/bin/python3
from timedrsa import Integer, multMod
from scipy.stats import pearsonr

def read_ciphertexts():
	"""
	Reads ciphertexts and timings from RSA_timing.txt and returns the following variables:
		N (Integer): RSA modulus.
		e (Integer): RSA public exponent.
		ciphertexts (list): List of ciphertexts.
		timings (list): List of timings for decrypting the corresponding ciphertext.
	"""
	with open("RSA_timing.txt") as file:
		lines = file.readlines()
		N = Integer(lines[0])  # RSA modulus
		e = Integer(lines[1])  # RSA public exponent
		ciphertexts = []  # List of ciphertexts
		timings = []  # List of timings
		for line in lines[3:]:
			c, t = line.split()
			timings.append(int(t))
			ciphertexts.append(Integer(c))
	
	return N, e, ciphertexts, timings

def process_ciphertexts(ciphertexts, result, N, help):
	vektor1 = []
	vektor2 = []
	result2_v = []
	result3_v = []
	for i,c in enumerate(ciphertexts):
		if help == 0:
			result[i], timing = multMod(result[i], result[i], N)
		
		temp = multMod(c, result[i], N)

		result2, timing2 = multMod(result[i], result[i], N)

		result3, timing3 = multMod(temp[0], temp[0], N)

		result2_v.append(result2)
		result3_v.append(result3)
		vektor1.append(timing2)
		vektor2.append(timing3)

	return (result2_v, result3_v, vektor1, vektor2)


N, e, ciphertexts, timings = read_ciphertexts()


d = [2]
help = 0


for i in range(65):	

	if i == 0:
		base = ciphertexts.copy()
		result2, result3, hoch2, hoch3 = process_ciphertexts(ciphertexts, base, N, help)
	else:
		if d[-1] == 0:
			base = result_a
		else:
			base = result_b
		result2, result3, hoch2, hoch3 = process_ciphertexts(ciphertexts, base, N, help)

	r1,p1 = pearsonr(timings , hoch2)
	r2,p2 = pearsonr(timings , hoch3)

	if r1 < r2:
		d.append(1)
		
	else:
		d.append(0)

	result_a = result2
	result_b = result3
	help = help + 1
	print(r2)
	print(r1)

print(d[1:])
