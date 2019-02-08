import time
import os
import uuid
import hashlib

import matplotlib.pyplot as plt


def test_difficulty(difficulty, prev_hash, new_hash, nonce, start_time):
    m = hashlib.sha256()
    temp = int(hashlib.sha256(str(nonce + prev_hash + new_hash).encode('utf-8')).hexdigest(), 16)
    num_attempts, target = 1, 2**difficulty

    while temp > target:
        nonce += 1
        # temp = hash(nonce + prev_hash + new_hash)
        temp = int(hashlib.sha256(str(nonce + prev_hash + new_hash).encode('utf-8')).hexdigest(), 16)

        if num_attempts % 10000000 == 0:
            if time.time() - start_time > 300:
                print('5-minutes up!')
                return -1, -1
            print(f'{num_attempts} have gone by')
        
        num_attempts += 1

    end = time.time()
    return end, num_attempts


def get_time(difficulty):
    prev_hash, new_hash = int(uuid.uuid4().hex[:16], 16), int(uuid.uuid4().hex[:16], 16)
    nonce = int(uuid.uuid4().hex[:8], 16)
    print(f'Prev: {prev_hash}, Nonce: {nonce}')
    start = time.time()
    end, num_attempts = test_difficulty(difficulty, prev_hash, new_hash, nonce, start)

    if end == -1 and num_attempts == -1:
        return None
    print(end - start)

    return end - start



def generate_data(difficulty, num_data_points=5):
    vector, num_attempts = [], 1
    while len(vector) < 5:
        if num_attempts == 3:
            return None

        result = get_time(difficulty)

        if result is not None:
            vector.append(result)
        else:
            num_attempts += 1

    return vector


def generate_plots(difficulty=235):
    i, results, temp = 0, [], []
    while temp is not None:
        temp = generate_data(difficulty - i)
        i += 1
        results.append(temp)
        

    fig_1, ax_1 = plt.subplots()
    ax_1.boxplot(results, vert=True)
    plt.show()

if __name__ == '__main__':
    generate_plots()
