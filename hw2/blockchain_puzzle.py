import time
import os
import uuid

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




def testDifficulty(difficulty, prevHash, nonce):
    #nonce = int(uuid.uuid4().hex[:8], 16)
    temp = hash(nonce + prevHash)
    while temp > difficulty:
        nonce += 1
        temp = hash(nonce + prevHash)
    end = time.time()
    return end

def main():
    prevHash = int(uuid.uuid4().hex[:8], 16)
    difficulty = 2**32.13
    start = time.time()
    print(start)
    nonce = int(uuid.uuid4().hex[:8], 16)
    end = testDifficulty(difficulty, prevHash, nonce)
    print(end - start)


#print(i)


def create_boxplot(vector):
    pass


