SCRIPT=`basename ${BASH_SOURCE[0]}`
CLIENT_DIR=$(cd `dirname $0` && pwd)
KVSTORE_SIZE=100
SLEEP_US=0

# Set fonts for Help.
NORM=`tput sgr0`
BOLD=`tput bold`
REV=`tput smso`

# Help function
function HELP {
  echo -e \\n"Help documentation for ${BOLD}${SCRIPT}.${NORM}"\\n
  echo -e "${REV}Basic usage:${NORM} ${BOLD}$SCRIPT -s KVSTORE_SIZE -t SLEEP_TIME${NORM}"\\n
  echo "${REV}-s${NORM}  --Sets the number of random keys ${BOLD}s${NORM} to be generated and used for tests."
  echo "${REV}-t${NORM}  --Sets the sleep time in microseconds ${BOLD}t${NORM} to sleep for letting updates propagate to all servers."
  echo -e "Example: ${BOLD}$SCRIPT -s 1000 -t 10${NORM}"\\n
  exit 1
}

# Check the number of arguments. If none are passed, print help and exit.
NUMARGS=$#
echo -e \\n"Number of arguments: $NUMARGS"
if [ $NUMARGS -eq 0 ]; then
  HELP
fi

### Start getopts code ###

#Parse command line flags
#If an option should be followed by an argument, it should be followed by a ":".
#Notice there is no ":" after "h". The leading ":" suppresses error messages from
#getopts. This is required to get my unrecognized option code to work.

while getopts :s:t:h FLAG; do
  case $FLAG in
    s)
      KVSTORE_SIZE=$OPTARG
      echo "-s used: $OPTARG"
      ;;
    t)
      SLEEP_US=$OPTARG
      echo "-t used: $OPTARG"
      ;;
    h)  #show help
      HELP
      ;;
    \?) #unrecognized option - show help
      echo -e \\n"Option -${BOLD}$OPTARG${NORM} not allowed."
      HELP
      ;;
  esac
done

shift $((OPTIND-1))  #This tells getopts to move on to the next argument.

echo "Building Cython client..."
python setup.py build_ext --inplace
echo "Appending the shared library name with 'lib'..."
mv client.so libclient.so
echo "Setting LD_LIBRARY_PATH to current directory..."
LD_LIBRARY_PATH="."
echo "Compiling correctness test for single client without failures..."
gcc -I/usr/include/python2.7 -o correctness_test_single_client correctness_test_single_client.c \
  -lpython2.7 -lclient -lpthread -DKVSTORE_SIZE="$KVSTORE_SIZE" -DSLEEP_TIME_US="$SLEEP_US"
echo "Compiling correctness test for single client with failure of single server..."
gcc -I/usr/include/python2.7 -o correctness_test_single_client_failures correctness_test_single_client_failures.c \
  -lpython2.7 -lclient -lpthread -DKVSTORE_SIZE="$KVSTORE_SIZE" -DSLEEP_TIME_US="$SLEEP_US"
echo "Compiling correctness test for single client with failure of two servers..."
gcc -I/usr/include/python2.7 -o correctness_test_single_client_failures_2servers correctness_test_single_client_failures_2servers.c \
  -lpython2.7 -lclient -lpthread -DKVSTORE_SIZE="$KVSTORE_SIZE" -DSLEEP_TIME_US="$SLEEP_US"
echo "Compiling correctness test for two clients without failures..."
gcc -I/usr/include/python2.7 -o correctness_test_two_clients correctness_test_two_clients.c \
  -lpython2.7 -lclient -lpthread -DKVSTORE_SIZE="$KVSTORE_SIZE" -DSLEEP_TIME_US="$SLEEP_US"
echo "Compiling correctness test for two clients with failure..."
gcc -I/usr/include/python2.7 -o correctness_test_two_clients_failure correctness_test_two_clients_failure.c \
  -lpython2.7 -lclient -lpthread -DKVSTORE_SIZE="$KVSTORE_SIZE" -DSLEEP_TIME_US="$SLEEP_US"

exit 0
