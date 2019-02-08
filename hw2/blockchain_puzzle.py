import time
import os
import uuid
import hashlib

import matplotlib


class BitcoinHeader():
    
    def __init__(self):
        self.hash_val = None
        self.timestamp = time.time()
        self.prev = None
        self.root = None
        self.nonce = self.generate_nonce()
        self.target = None
    
    def generate_nonce(self, length=8):
        return uuid.uuid4().hex[:length]
    
    def set_new_nonce(self):
        self.nonce = self.generate_nonce()




def test_difficulty(difficulty, prev_hash, new_hash, nonce, start_time):
    m = hashlib.sha256()
    temp = hash(nonce + prev_hash + new_hash)
    num_attempts = 1

    while temp > difficulty:
        nonce += 1
        temp = hash(nonce + prev_hash + new_hash)

        if num_attempts % 50000000 == 0:
            if time.time() - start_time > 300:
                print('5-minutes up!')
                break
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
    print(end - start)

    return end - start, num_attempts



def generate_data(difficulty, num_data_points=5):
    vector = [get_time(difficulty) for _ in range(num_data_points)]

    print(vector)


def main():
    difficulty = 2**35
    generate_data(difficulty)


if __name__ == '__main__':
    main()
