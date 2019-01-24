import hashlib
import random


def generate_different_ascii_str(str_len=50):
    first = ''.join([chr(random.randint(0, 255)) for _ in range(str_len)])
    second = ''.join([chr(random.randint(0, 255)) for _ in range(str_len)])
    
    while first == second:
        first, second = random.randint(0, 255), random.randint(0, 255)
    
    return first, second



def equal_sha(first, second):
    first_hash = hashlib.sha1(first.encode('utf-8')).hexdigest()
    second_hash = hashlib.sha1(second.encode('utf-8')).hexdigest()

    return first_hash[:10] == second_hash[:10]


def find_sha_pair():
    first, second = generate_different_ascii_str()
    num_attempts = 1

    while not equal_sha(first, second):
        first, second = generate_different_ascii_str()
        num_attempts += 1
        if num_attempts % 100000 == 0:
            print(f'Hit another multiple of 100,000: {num_attempts}')

    return first, second, num_attempts


if __name__ == '__main__':
    print('Found (%s, %s) pair in %i attempts' % find_sha_pair())