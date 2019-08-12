# Inventory README
## Install
### Pre-requisites
#### Python 3.7
Preferred install mode on Mac: brew
See [Brew and Python](https://docs.brew.sh/Homebrew-and-Python)
See also being sure correct 3.7 available on your path.

To run all `brew` and `pip3` install commands, you have to be logged in as the user who installed brew initially. (you run the program as a lower privileged user)

` brew link --overwrite --dry-run python3`
and that `/usr/local/bin` precedes `/bin` in the path of the user who will be running this software. (as well as the installer)

To simplify, the end user can create python's  `virtualenv` in their local directory, which allows an isolated environment.

#### pymysql for 3.7
`pip3 install pymysql` as the brew 
**Important Note** these utilities may run as services. Create non-admin users to run these services. Set up the path from an admin user.

### installation
Download  [inventory BDRC_DBLib](https://github.com/buda-base/asset-manager/tree/backlog-inventory/BDRC_DBLib) into `<BDRC_DBLib directory>`

As the owner of brew on the machine which will run 'inventory' run:
```
cd <BDRC_DBLib directory>
python3 setup.py install
```
You have to be the user who has installed brew for permissions to work.
## Configuration
The running user needs to have 

