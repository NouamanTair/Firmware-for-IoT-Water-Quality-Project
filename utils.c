#include "utils.h"

double averageArray(int *arr, int length) {
    long sum = 0;
    for (int i = 0; i < length; i++) {
        sum += arr[i];
    }
    return (double)sum / length;
}
