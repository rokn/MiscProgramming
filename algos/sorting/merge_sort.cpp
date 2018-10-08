#include "sorting.h"
#include <algorithm>

const int INSERTION_SIZE = 3;

void merge(int *arr, int first, int second, int end);

void merge_sort(int *arr, int n) {
	if(n <= 1) return;
	int i = 0;
	while(i < n) {
			insertion_sort(arr+i, std::min(n-i, INSERTION_SIZE));
		i += INSERTION_SIZE;
	}

	int ms = INSERTION_SIZE;
	i = 0;

	//while (ms >= n) {
		merge(arr, i, i+ms, i+2*ms);
	//}
}

void merge(int *arr, int first, int second, int end) {
	int *temp = new int[end - first];
	int i = first;
	int j = second;
	int t = 0;
	int n;

	while (i < second && j < end) {
		if (arr[i] < arr[j]) {
			n = arr[i++];
		} else {
			n = arr[j++];
		}
		temp[t++] = n;
	}

	while(i < second) {
		temp[t++] = arr[i++];
	}

	i = first;
	while(i < first + t) {
		arr[i] = temp[i - first];
		i++;
	}
}
