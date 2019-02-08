import time
import os
import uuid
import hashlib

import matplotlib.pyplot as plt

from bitcoin_header import BitcoinHeader


def test_target(header):
    nonce, prev_hash, new_hash = header.get_values()
    byte_val = str(nonce + prev_hash + new_hash).encode('utf-8')
    temp = int(hashlib.sha256(byte_val).hexdigest(), 16)
    num_attempts, target = 1, 2**header.target
    start_time = time.time()

    while temp > target:
        nonce += 1
        byte_val = str(nonce + prev_hash + new_hash).encode('utf-8')
        temp = int(hashlib.sha256(byte_val).hexdigest(), 16)

        if num_attempts % 1000000 == 0:
            if time.time() - start_time > 300:
                print('5-minutes up!')
                return None

        num_attempts += 1

    return time.time() - start_time


def generate_data(target, num_data_points=5):
    vector, num_attempts = [], 1
    while len(vector) < num_data_points:
        if num_attempts == 3:
            return None

        header = BitcoinHeader(target)
        result = test_target(header)

        if result is not None:
            vector.append(result)
        else:
            num_attempts += 1

    return vector


def generate_plots(target=235):
    i, results, temp = 0, [], generate_data(target)
    
    while temp is not None and i < 10:
        print(f'Finished: {target-i}')
        i += 1
        results.append(temp)
        temp = generate_data(target - i)

    fig, ax = plt.subplots()
    ax.boxplot(results, vert=True)
    ax.set_title('Blockchain Puzzle')
    ax.set_xlabel('Difficulty')
    ax.set_ylabel('Time (s)')
    plt.show()


if __name__ == '__main__':
    generate_plots()
