#include "ring.h"
// 1
int* fillRing(int n) {
	int* ring = new int[n];
	for(int i = 0; i < n; i++) {
		ring[i] = i;
	}

	return ring;
}

// 2
int add(int left, int right, int n) {
	if(left > n || right > n) {
		return -1;
	}

	return (left + right) % n;
}

// 3
int subract(int left, int right, int n) {
	if(left > n || right > n) {
		return -1;
	}

	int res = left - right;
	if(res < 0) {
		res = n + res;
	}

	return res;
}

// 4
int multiply(int left, int right, int n) {
	if(left > n || right > n) {
		return -1;
	}

	return (left * right) % n;
}

// 5
int** fillPairs(int n) {
	int** result = new int*[2];
	result[0] = new int[n];
	result[1] = new int[n];
	int c = 0;

	for(int i = 1; i < n; i++) {
		for(int j = 1; j < n; j++) {
			unsigned int mult = j*i;
			if((mult % n) == 1) {
				result[0][c] = i;
				result[1][c] = j;
				c++;
				break;
			}
		}
	}

	result[0][c] = -1; // Last element;
	result[1][c] = -1; // Last element;

	return result;
}

// 6
int findReciprocal(int numb, int** pairs) {
	int res = -1;
	int i = 0;
	while(pairs[0][i] != -1) {
		if(pairs[0][i] == numb) {
			res = pairs[1][i];
			break;
		}
		i++;
	}

	return res;
}

// 8
int divide(int left, int right, int n, int** pairs) {
	int rightReciprocal = findReciprocal(right, pairs);
	if(rightReciprocal == -1) {
		return -1;
	}

	return multiply(left, rightReciprocal, n);
}

// 9
int powerFirst(int numb, int pow, int n) {
	int curr = numb;
	int k = 1;

	do {
		k++;
		curr = multiply(curr, numb, n);
	} while(curr != 1);

	int realPower = pow % k;
	int res = numb;

	for (int i = 1; i < realPower; ++i) {
		res = multiply(res, numb, n);
	}

	return res;
}

// 10
int powerSecond(int numb, int pow, int n) {
	if(numb == 0) {
		return 0;
	}

	int firstBit = 31;
	while((pow & firstBit) != 0) {
		firstBit--;
	}

	int curr = 3;
	int res = 1;
	for (int i = 0; i < firstBit; ++i) {
		bool hasOne = pow & (1 << i); // Check if the power has a 1 in i-th bit
		if(hasOne) {
			res = multiply(res, curr, n);
		}

		curr = multiply(curr, curr, n);
	}

	return res;
}

// 11
bool isField(int n) {
	// Just check if the n is prime for it to be a field
	for (int i = 2; i < n/2; ++i) {
		if(n % 2 == 0) {
			return false;
		}
	}

	return true;
}

