import hashlib
import random


def generate_ascii_str(str_len=50, num=1000000):
    current = 0
    while current < num:
        ascii_str = ''.join([chr(random.randint(0, 255)) for _ in range(str_len)])
        yield hashlib.sha1(ascii_str.encode('utf-8')).hexdigest()[:10], ascii_str
        current += 1


def find_sha_pair():
    num_attempts, found, hashes = 0, False, {}

    while True:
        for hash_str, ascii_str in generate_ascii_str():
            num_attempts += 1
            if hash_str in hashes:
                return ascii_str, hashes[hash_str], num_attempts
            hashes[hash_str] = ascii_str
        else:
            print('Went through a million ascii strings... Trying again')



if __name__ == '__main__':
    print('First:\n%s\nSecond:\n%s\nPair in %i attempts' % find_sha_pair())