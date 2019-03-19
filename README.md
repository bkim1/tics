# Topics in Computer Systems 

This repository holds any coding assignments and projects for CSCI3394.

---

Homework Group: 2

Authors: Branden Kim, Chris Zafanella, Griffin Elliott, Rocky Chiu, Sean McWilliams

Professor: Lewis Tseng

---

## Homeworks

#### Homework 1

Utilizes the birthday paradox to find a collision using the SHA-1 encryption.


#### Homework 2

Estimates the time of solving a hash-based puzzle given certain degrees of difficulty. Outputs a box-plot to show the results.

#### Homework 3

Implements a simple distributed system for verifying hash-based puzzle solutions. Includes a simple server (verifier) and client (solver) where the client sends its solved puzzle to the server to get confirmation that it is a valid solution.



## How to Install
1. Clone the repo using `git clone https://github.com/bkim1/tics.git`
2. Navigate to the cloned repo using `cd tics`
3. If you already have a virtual environment created, then skip to step 4
   1. Create a virtual environment using `python3 -m venv venv`
4. Activate the virtual environment using `source venv/bin/activate`
5. Install the dependencies using `pip install -r requirements.txt`
6. Look at the specific homework folder README's for any further instruction