import time
import os
import uuid
import hashlib

import matplotlib.pyplot as plt

from bitcoin_header import BitcoinHeader


def test_target(header):
    nonce, prev_hash, new_hash = header.get_values()
    temp = int(hashlib.sha256(str(nonce + prev_hash + new_hash).encode('utf-8')).hexdigest(), 16)
    num_attempts, target = 1, 2**header.target
    start_time = time.time()

    while temp > target:
        nonce += 1
        temp = int(hashlib.sha256(str(nonce + prev_hash + new_hash).encode('utf-8')).hexdigest(), 16)

        if num_attempts % 10000000 == 0:
            if time.time() - start_time > 300:
                print('5-minutes up!')
                return None
            print(f'{num_attempts} have gone by')
        
        num_attempts += 1

    return time.time() - start_time


def generate_data(target, num_data_points=5):
    vector, num_attempts = [], 1
    while len(vector) < 5:
        if num_attempts == 3:
            return None

        header = BitcoinHeader(target)
        result = test_target(header)

        if result is not None:
            vector.append(result)
        else:
            num_attempts += 1

    return vector


def generate_plots(target=240):
    i, results, temp = 0, [], []
    # while temp is not None:
    while i < 3:
        temp = generate_data(target - i)
        i += 1
        results.append(temp)
        

    fig_1, ax_1 = plt.subplots()
    ax_1.boxplot(results, vert=True)
    plt.show()

if __name__ == '__main__':
    generate_plots()
