#!/usr/bin/env python

""" Copyright (c) 2010 Ma Can <ml.macana@gmail.com>

This is the core TDWPL client module, enjoy it :)
"""

import signal, os, time, errno, datetime
import sys, traceback
import getopt
import pdb
import cmd
import getpass
import subprocess
import ConfigParser
import socket, random
from threading import Thread

#sys.path.append("../../lib/py/")
sys.path.insert(0, "../../lib/py/")

from hive_service import ThriftHive
from hive_service.ttypes import HiveServerException
from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

import tdw

RestrictedGlobals = dict(__builtins__ = __builtins__)
RestrictedGlobals['__import__'] = None

use_readline = True

try:
    import shlex
except ImportError, ie:
    print "Fatal Error: Import failed (%s)" % (ie)

try:
    import rlcompleter
    import readline
except ImportError, ie:
    print "Warning: Import failed (%s)" % (ie)
    use_readline = False

class bcolors:
    '''Color-full mode.'''
    HEADER = '\033[36m'
    OKPINK = '\033[35m'
    OKBLUE = '\033[34m'
    OKGREEN = '\033[32m'
    WARNING = '\033[33m'
    FAIL = '\033[41m'
    ENDC = '\033[0m'
    mode = True
    def __init__(self):
        pass

    def flip(self):
        '''Flip the mode'''
        if self.mode == True:
            self.mode = False
        else:
            self.mode = True
        
    def print_warn(self, string):
        '''Print the warning message in WARNING color'''
        if self.mode:
            print bcolors.WARNING + str(string) + bcolors.ENDC
        else:
            print str(string)

    def print_err(self, string):
        '''Print the error message in FAIL color'''
        if self.mode:
            print bcolors.FAIL + str(string) + bcolors.ENDC
        else:
            print str(string)

    def print_ok(self, string):
        '''Print the ok message in OK color'''
        if self.mode:
            print bcolors.OKGREEN + str(string) + bcolors.ENDC
        else:
            print str(string)

    def print_pink(self, string):
        '''Print in the PINK color'''
        if self.mode:
            print bcolors.OKPINK + str(string) + bcolors.ENDC
        else:
            print str(string)
def getExceptTrackInfo(): 
    import sys
    import traceback
    info = sys.exc_info()
    error_info=""
    for file, lineno, function, text in traceback.extract_tb(info[2]):
        error_info += "File " + "\"" + str(file) + "\"" +",line: "+str(lineno)+",in "+str(function) + '\n'
        error_info += "    "+str(text) + '\n'
    error_info = error_info + str(info[0]) + ":" + str(info[1])
    return error_info
#add by wenson
def write_except_msg(tdw, msg):
    try:
        if tdw.mark and tdw.mark.logger:
            tdw.WriteLog(msg) 
            tdw.finishMark()
        else:
            pass
    except Exception,e:
        pass
#wenson add ended

#added by michealxu
def handler(signum,frame):
    raise TimeOutError, "Audit TimeOut"
#add end

def main(argv):
    '''I am the superman, HaHaHa...'''
    try:
        opts, args = getopt.getopt(argv, "hd:f:a:gc:e:ls:u:x:y:j:v:p:m:w:", 
                                   ["help", "file=", "action=", "getcols=", "session=", 
                                    "authid=", "user=", "passwd=", "jobid=", 
                                    "dbname=", "server=", "port=",
                                    "exec=","mark=","wait=","recont=","waitco="])
    except getopt.GetoptError:
        print "TDW-00004 FAILED: TDWClient input arguments error (GetoptError)"	
        return 4

    fname = None
    action = None
    session = None
    authid = None
    debug = 0
    user = None
    passwd = None
    server = None
    port = None
    dbname = None
    jobid = -1
    old = None
    exec_string = ""
    mark_param = None
    
    #added by michealxu
    argvlist = []
    module = []
    exitid = 0
    iscon = []
    waittime = 5
    overtime = 150
    retrytime = 6
    waittime2 = 60
    reExeNeedSet = []
    reExeNeedUse = None
    #add end
    
    ###add by payniexiao in 20130108 for get sql cols
    getcolsqls = ""          
    ###end
	
    drop = 1
    for opt, arg in opts:
        if opt in ("-f", "--file"):
            fname = arg
        elif opt in ("-a", "--action"):
            action = arg
            if action == "create":
                drop = 0
                # ignore any sessions user supplied
                session = None
        elif opt in ("-s", "--session"):
            session = arg
            drop = 0
        elif opt in ("-u", "--authid"):
            authid = arg
        elif opt in ("-x", "--user"):
            user = arg
        elif opt in ("-y", "--passwd"):
            passwd = arg
        elif opt in ("-j", "--jobid"):
            jobid = arg
        elif opt in ("-d", "--dbname"):
            dbname = arg
        elif opt in ("-v", "--server"):
            server = arg
        elif opt in ("-p", "--port"):
            port = arg
        elif opt in ("-l"):
            action = "xshell"
        elif opt in ("-e", "--exec"):
            exec_string = arg
            action = "exec"
        elif opt in ("-m","--mark"):
            mark_param = arg
        elif opt in ("-w","--wait"):
            waittime = arg    
        elif opt in ("--recont"):
            retrytime = arg  
            #print "retrytime: %s " %(retrytime)
        elif opt in ("--waitco"):
            waittime2 = arg      
            #print "waittime2: %s " %(waittime2)    
        elif opt in ("-h", "--help"):
            print_help()
            sys.exit()            
        ###add by payniexiao in 20130108 for get sql cols
        elif opt in("-gc", "--getcols"):
 	    ##print "get columns now!!!!!!!!"
	    action = "getcols"
            getcolsqls = arg
        ###end

        if dbname == None:
            dbname = "default_db"
    
    #add by wenson
    __tdw = tdw.TDW()
    try:
        mark_param_list = None
        if not mark_param or len(mark_param.strip().split(";")) < 2:
            mark_param_list = ['test','test']
        else:
            mark_param_list = mark_param.strip().split(";")
            first_param = str(mark_param_list[0])
            second_param = str(mark_param_list[1])
            if first_param.strip() == "":
                mark_param_list[0] = 'test'
            if second_param.strip() == "":
                mark_param_list[1] = 'test'    
        __tdw.init_mark(mark_param_list)
    except Excetion,e:
        #the code should be modifieFailed
        print "WARNING: Error getting tdw log module."
    #wenson add ended
    try:
        if len(opts) == 0:
            print "No arguments, we fall back to shell mode now ..."
            action = "shell"
        if action == None:
            action = "shell"
            
        if action == "show" or action == "create":
            print "Passing the arg check."
        elif action == "testshell":
            print "Entering test shell mode now ..."
            tdwpl_shell().cmdloop("Welcome to TDW PL Shell, "
                                  "for help please input ? or help")
            sys.exit()
        elif action == "xshell":
            print "Entering extend shell mode now ..."
            tdwpl_shell('x').cmdloop("Welcome to TDW PL Shell, "
                                     "for help please input ? or help")
            sys.exit()
        elif action == "shell":
            print "Entering shell mode now ..."
            tdwpl_shell("m", server, port, 
                        user, passwd).cmdloop("Welcome to TDW PL Shell, "
                                              "for help please input ? or help")
            sys.exit()
        elif action == "get":
            if session == None:
                print "TDW-00004 FAILED: TDWClient input arguments error (Invalid TDW/PL get command)"				
                #print "Invalid TDW/PL get command."
                print_help()
                return 4
        elif action == "compile":
            if user == None or passwd == None:
                print "TDW-00004 FAILED: TDWClient input arguments error (Invalid username or password)"			  
                #print "Invalid username or password."
                return 4
            # we should grep the static sql strings and send it to hive server
            tdwpl_compile()
            
        #add by payniexiao in 20130107 for get output columns of a SQL
        elif action == 'getcols':
            if user == None or passwd == None:
                print "TDW-00004 FAILED: TDWClient input arguments error (Invalid username or password)"
                write_except_msg(__tdw, "TDW-00004 FAILED: TDWClient input arguments error (Invalid username or password)")
                return 4  
        ##add end
        elif action == "exec":
            if user == None or passwd == None:
                #print "Invalid username or password."
                print "TDW-00004 FAILED: TDWClient input arguments error (Invalid username or password)"
                #add by wenson
                write_except_msg(__tdw, "TDW-00004 FAILED: TDWClient input arguments error (Invalid username or password)")
                #wenson add ended
                return 4
            # we should split the user exec string and execute them one by one
            home = os.getcwd() + os.path.sep + 'mark.conf' 
            if os.path.exists(home):
                cfg = tdw.Configure(home)
                is_support_semicolon = cfg.getValue('is_support_semicolon')
                if is_support_semicolon == 'Y':
                    start = 0
                    end = 0
                    exec_list=[]
                    exec_length = len(exec_string)
                    while start >= 0 and end >= 0 and start < exec_length and end < exec_length:
                        end = exec_string.find(';',start)
                        #print end
                        if end > 0:
                            count1 = exec_string.count("'", start, end)
                            #print count1
                            count2 = exec_string.count('"', start, end)
                            #print count2
                            while end > 0 and (count1%2 == 1 or count2%2 == 1):
                                end = exec_string.find(';',end + 1)
                                count1 = exec_string.count("'", start, end)
                                count2 = exec_string.count('"', start, end)
                            if end == -1:
                                #print exec_string[start :]
                                exec_list.append(exec_string[start :])
                            elif end > 0:
                                #print exec_string[start : end]
                                exec_list.append(exec_string[start : end])
                                start = end+1
                        elif end == 0:
                            #print exec_string[start : end]
                            exec_list.append(exec_string[start : end])
                            start = end+1
                        elif end == -1:
                            #print exec_string[start :]
                            exec_list.append(exec_string[start :])
                    exec_string = exec_list
                    #print exec_string
                else:
                    exec_string = exec_string.split(';')
            else:
                exec_string = exec_string.split(';')
            # exec_string = exec_string.split(';')
       	    # added by michealxu
            
            for i in range(0,len(exec_string)):  
                iscon.append(-1)
                if (exec_string[i].find(".py")!=-1):
                    exec_string[i] = exec_string[i].strip()
                    if exec_string[i].startswith("@"):
                        exec_string[i] = exec_string[i].replace("@","")
                        exec_string[i] = exec_string[i].strip()
                    elif exec_string[i].startswith("run"):	
                        exec_string[i] = exec_string[i].replace("run","")
                        exec_string[i] = exec_string[i].strip()
                    elif exec_string[i].startswith("start"):	
                        exec_string[i] = exec_string[i].replace("start","")	
                        exec_string[i] = exec_string[i].strip()
                    else:
                        argvlist.append("")		
                        module.append("")
                        iscon[i] = 0	
                        continue                            						
                    sys.path.append(os.path.dirname(exec_string[i].split(" ")[0]))
                    
                    try:
                        sys.path.append("%s/lib/" 
                                        % ("~/.tdwpl/".replace("~", 
                                                               os.getenv("HOME"))))
                        sys.path.append("%s/lib/global" 
                                        % ("~/.tdwpl/".replace("~", 
                                                               os.getenv("HOME"))))
                        sys.path.append("%s/lib/%s" 
                                        % ("~/.tdwpl/".replace("~", 
                                                               os.getenv("HOME")),user))
                    except OSError,oe:
                        print "TDW-00202 FAILED: OS error (OSError %s)" % (oe.strerror)
                        #add by wenson
                        write_except_msg(__tdw, "TDW-00202 FAILED: OS error (OSError %s)" % (oe.strerror))
                        #wenson add ended
                        return 202
                                                                  
                    tmpfname = exec_string[i].split()			
                    argvlist.append(tmpfname)	
                    
                    #os.getcwd()+'/'+
                    if os.access(tmpfname[0],os.F_OK) == False: 
                        print "TDW-00005 FAILED: TDWClient python file read error (%s not find)" %(tmpfname[0])
                        #add by wenson
                        write_except_msg(__tdw, "TDW-00005 FAILED: TDWClient python file read error (%s not find)" %(tmpfname[0]))                        
                        #wenson add ended
                        return 5						
                    try:					
                        module.append(__import__(os.path.basename(tmpfname[0]).rstrip("py").rstrip(".")))                  
                    except SyntaxError, se:
                        print "TDW-00002 FAILED: TDWClient compile python error (SynaxError : %s %s Line %d Offset %d)" %(se.filename,se.text,se.lineno,se.offset)	
                        #add by wenson
                        write_except_msg(__tdw, "TDW-00002 FAILED: TDWClient compile python error (SynaxError : %s %s Line %d Offset %d)" %(se.filename,se.text,se.lineno,se.offset))    
                        #wenson add ended
                        return 2						
                    except TypeError, te:
                        print "TDW-00002 FAILED: TDWClient compile python error (TypeError  : %s)" % (te.strerror)
                        #add by wenson
                        write_except_msg(__tdw, "TDW-00002 FAILED: TDWClient compile python error (TypeError  : %s)" % (te.strerror))
                        #wenson add ended
                        return 2          
                else:
                    argvlist.append("")		
                    module.append("")	
                    iscon[i] = 0				
            #add end	
        elif action == "execf":
            if action == None or fname == None:
                #print "Invalid TDW/PL action or file name."
                print "TDW-00004 FAILED: TDWClient input arguments error (Invalid TDW/PL action or file name)"			
                print_help()
                return 4
            else:
                sys.path.append(os.path.dirname(fname))
                sys.path.append("%s/lib/" 
                                % ("~/.tdwpl/".replace("~", 
                                                       os.getenv("HOME"))))
                sys.path.append("%s/lib/global" 
                                % ("~/.tdwpl/".replace("~", 
                                                       os.getenv("HOME"))))
                sys.path.append("%s/lib/%s" 
                                % ("~/.tdwpl/".replace("~", 
                                                       os.getenv("HOME")),
                                   user))
                module = __import__(os.path.basename(fname).rstrip("py").rstrip("."))
        else:
            #print "Invalid actions."
            print "TDW-00004 FAILED: TDWClient input arguments error (Invalid actions)"				
            print_help()
            return 4

        # check the arguments
        if server == None or port == None:
            #print "Invalid arguments, no server or port supplied."
            print "TDW-00004 FAILED: TDWClient input arguments error (Invalid arguments, no server or port supplied)"
            #add by wenson
            write_except_msg(__tdw, "TDW-00004 FAILED: TDWClient input arguments error (Invalid arguments, no server or port supplied)")            
            #wenson add ended
            return 4
        if user == None or passwd == None:
            #print "Invalid arguments, no user or passwd supplied."
            print "TDW-00004 FAILED: TDWClient input arguments error (Invalid arguments, no user or passwd supplied)"	
            #add by wenson
            write_except_msg(__tdw, "TDW-00004 FAILED: TDWClient input arguments error (Invalid arguments, no user or passwd supplied)")                 
            #wenson add ended
            return 4

        #add by cherry start
        try:
            resultlist = socket.getaddrinfo(server, None)
            ipcounter = 0
            iplist = [i[4][0] for i in resultlist]
            iplist = list(set(iplist))
            ip = None
            #for i in iplist:
            #    print "%s"%(i)
            ipcounter = len(iplist)
            time1 = 0
            time2 = 0
            time3 = 0
            time4 = 0
        except socket.error , gaie:
            print "TDW-00001 FAILED: TDWClient connect server %s error: %s"%(server, gaie[1])
            write_except_msg(__tdw, "TDW-00001 FAILED: TDWClient connect server %s error: %s"%(server, gaie[1]))
            exc_type, exc_value, exc_traceback = sys.exc_info()
            return 1
        #add by cherry end
        try:
            #add by cherry start
            time1 = random.randint(0, ipcounter-1)
            ip1 = iplist[time1]
            ip = ip1
            print "connect to hive ip1:%s"%(ip1)
            transport = TSocket.TSocket(ip1, int(port))
            #add by cherry end
            #transport = TSocket.TSocket(server, int(port))
            transport = TTransport.TBufferedTransport(transport)
            protocol = TBinaryProtocol.TBinaryProtocol(transport)
            client = ThriftHive.Client(protocol)
            transport.open()
        except HiveServerException, hsx:
            transport.close()
            print "TDW-00001 FAILED: TDWClient connect server error (%s)" %(hsx.message)
            #add by wenson
            write_except_msg(__tdw, "TDW-00001 FAILED: TDWClient connect server error (%s)" %(hsx.message))
            #wenson add ended
            return 1
        except Thrift.TException, tx:
            transport.close()
            print "TDW-00201 FAILED: Network communication error (%s)" %(tx.message)
            #add by wenson
            write_except_msg(__tdw, "TDW-00201 FAILED: Network communication error (%s)" %(tx.message))
            #wenson add ended
            return 201
        except:
            print "TDW-00001 FAILED: TDWClient connect server error: "
            #add by wenson
            write_except_msg(__tdw, "TDW-00001 FAILED: TDWClient connect server error: ")
            #wenson add ended
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            return 1
            
        #added by michealxu  
        signal.signal(signal.SIGALRM,handler);
        #print "start audit: "
        #print time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time())) 
        #sys.stdout.flush()
        waittimeint = int(waittime)
        try:
            signal.alarm(overtime)
            res = client.audit(user, passwd, dbname)
            signal.alarm(0)
        except:
            info = sys.exc_info()
            print "auditfailed 1 sleep %d seconds %s"  %(waittimeint, info)
            #print time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
            #sys.stdout.flush()
            transport.close()
            if waittimeint == 0:
                print "TDW-00100 FAILED: TDWServer audit user error"
                return 100                 
            time.sleep(waittimeint)
            try:
                #print time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
                #sys.stdout.flush()
                #add by cherry start
                time2 = random.randint(0, ipcounter-1)
                while ipcounter>0 and time2 == time1:
                    time2 = random.randint(0, ipcounter-1)
                ip2 = iplist[time2]
                ip = ip2
                print "connect to hive ip2:%s"%(ip2)
                transport = TSocket.TSocket(ip2, int(port))
                #add by cherry end
                #transport = TSocket.TSocket(server, int(port))
                transport = TTransport.TBufferedTransport(transport)
                protocol = TBinaryProtocol.TBinaryProtocol(transport)
                client = ThriftHive.Client(protocol)
                transport.open()
                #print time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
                #sys.stdout.flush()
                signal.alarm(overtime)
                res = client.audit(user, passwd, dbname)
                signal.alarm(0)
            except:
                info = sys.exc_info()
                print "auditfailed 2 sleep %d seconds %s"  %(waittimeint, info)
                #print time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
                #sys.stdout.flush()
                transport.close()
                time.sleep(waittimeint)
                try:
                    #print time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
                    #sys.stdout.flush()
                    #add by cherry start
                    time3 = random.randint(0, ipcounter-1)
                    while ipcounter>1 and (time3 == time1 or time3 == time2):
                        time3 = random.randint(0, ipcounter-1)
                    ip3 = iplist[time3]
                    ip = ip3
                    print "connect to hive ip3:%s"%(ip3)
                    transport = TSocket.TSocket(ip3, int(port))
                    #add by cherry end
                    #transport = TSocket.TSocket(server, int(port))
                    transport = TTransport.TBufferedTransport(transport)
                    protocol = TBinaryProtocol.TBinaryProtocol(transport)
                    client = ThriftHive.Client(protocol)
                    transport.open()
                    #print time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
                    #sys.stdout.flush()
                    signal.alarm(overtime)
                    res = client.audit(user, passwd, dbname)
                    signal.alarm(0)
                except:
                    info = sys.exc_info()
                    print "auditfailed 3 sleep %d seconds %s"  %(waittimeint, info)
                    #print time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
                    #sys.stdout.flush()
                    transport.close()
                    time.sleep(waittimeint)
                    try:
                        #print time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
                        #sys.stdout.flush()
                        #add by cherry start
                        time4 = random.randint(0, ipcounter-1)
                        while ipcounter>2 and (time4 == time1 or time4 == time2 or time4 == time3):
                            time4 = random.randint(0, ipcounter-1)
                        ip4 = iplist[time4]
                        ip = ip4
                        print "connect to hive ip4:%s"%(ip4)
                        transport = TSocket.TSocket(ip4, int(port))
                        #add by cherry end
                        #transport = TSocket.TSocket(server, int(port))
                        transport = TTransport.TBufferedTransport(transport)
                        protocol = TBinaryProtocol.TBinaryProtocol(transport)
                        client = ThriftHive.Client(protocol)
                        transport.open()
                        #print time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
                        #sys.stdout.flush()
                        signal.alarm(overtime)
                        res = client.audit(user, passwd, dbname)
                        signal.alarm(0)
                    except:
                        info = sys.exc_info()    
                        print "auditfailed 4 sleep %d seconds %s"  %(waittimeint, info)
                        #print time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
                        #sys.stdout.flush()
                        transport.close()
                        print "TDW-00100 FAILED: TDWServer audit user error"
                        return 100 
        __tdw.init_connect(server, ip, int(port), int(retrytime),transport,protocol,user,passwd, dbname, int(waittime2)) 
        #add end
            
        #print "ggggget audit"
        if check_result_int(res) == "failed":
            #print "Audit failed, exiting"
            transport.close()
            print "TDW-00100 FAILED: TDWServer audit user error"
            #add by wenson
            write_except_msg(__tdw, "TDW-00100 FAILED: TDWServer audit user error")
            #wenson add ended
            return 100  

        if session != None:
            res = client.requireSession(session, authid)
            #print "ggggget requireSession"
            #print "Require Session " + session + " " + check_result(res)
            if check_result(res) == "failed":
                transport.close()
                print "TDW-00101 FAILED: TDWServer requireSession error (Require Session %s failed)" %(session)	
                #add by wenson                
                write_except_msg(__tdw, "TDW-00101 FAILED: TDWServer requireSession error (Require Session %s failed)" %(session))                
                #wenson add ended
                return 101
        else:
            try:		
                old = client.createSession("")
                print "New Session " + " ".join(old)
                
                session = old[0]
                authid = old[1]
                __tdw.init_session(session,authid)
                # add by joeyli begin
                # auto run a set command
                res = client.execute("set tdw.username="+user)
                # add by joeyli end
                #print "ggggget setusername"
                
            except HiveServerException, hsx:
                transport.close()
                print "TDW-00102 FAILED: TDWServer createSession error (%s)" %(hsx.message)
                #add by wenson
                write_except_msg(__tdw, "TDW-00102 FAILED: TDWServer createSession error (%s)" %(hsx.message))
                #wenson add ended
                return 102
            except Thrift.TException, tx:
                transport.close()
                print "TDW-00201 FAILED: Network communication error (%s)" %(tx.message)
                #add by wenson
                write_except_msg(__tdw, "TDW-00201 FAILED: Network communication error (%s)" %(tx.message))
                #wenson add ended
                return 201
            
        if action == "del":
            # drop the session by the framework
            drop = 1
        elif action == "get":
            print_warning(session, "getJobStatus")
            res = client.getJobStatus(int(jobid))
            print "getJobStatus:"
            printlist(res)
        elif action == "set":
            print_warning(session, "configJob")
            res = client.configJob("type=ONESHOT;ti=10;")
            print "configJob " + check_result_int(res)
        elif action == "kill":
            print_warning(session, "killJob")
            res = client.killJob()
            print "killJob " + check_result_int(res)
        elif action == "show":
            res = client.showSessions()
            print "Session List:"
            printlist(res)
        
        ###add by payniexiao in 20130108 for get output cols for sql
        elif action == "getcols":
            #print "get output columns for a sql"
            getcolsqlarray = getcolsqls.split(";")
            sqlnum = len(getcolsqlarray)
            try:
                for i in range(0, sqlnum):
                    if(i == sqlnum - 1):
                        res = client.getSchemaForExplain(getcolsqlarray[i])
                        '''Print a list'''
                        if type(res) == type(list()):
                            index = 0
                            retstring = ""
                            listlen = len(res)
                            for a in res:
                                nametype = a.split(":")
                                nametypelen = len(nametype)
                                if nametypelen < 2:
                                    index = index + 1
                                    continue
                                itemstring = "col%d %s" %(index, nametype[1])
                                if index == listlen - 1:
                                    retstring = retstring + itemstring
                                else:
                                    retstring = retstring + itemstring + ";"
                                index = index + 1
                            print retstring
                        elif type(res) == type(str()):
                            print res
                        ###printlist(res) 
                    else:
                        client.execute(getcolsqlarray[i])
                                               
            except HiveServerException, hsx:
                
                print "TDW-00105 FAILED: TDWServer run SQL error (session: %s query: %s) (%s)" %(session,getcolsqls,hsx.message)
                drop = 1
                exitid = 105
                try:
                    client.dropSession(session, authid)
                except Exception, ee:
                    drop = 1                    
                transport.close()
                return exitid
            
            except Thrift.TException, tx:
                print "TDW-00201 FAILED: Network communication error (session: %s query: %s) (%s)" %(session,getcolsqls,tx.message) 
                drop = 1
                exitid = 201
                try:
                    client.dropSession(session, authid)
                except Exception, ee:
                    drop = 1
                transport.close()
                return exitid
            
            except:
                drop = 1    
                exitid = 105
                print "TDW-00105 FAILED: TDWServer run SQL error: (session: %s query: %s)" %(session,getcolsqls)
                try:
                    client.dropSession(session, authid)
                except Exception, ee:
                    drop = 1
                    #print "dropSession failed: %s"  %(session)    
                #print "drop: %s" %(session)
                transport.close()
                return exitid                       
        ##add end
            
        elif action == "exec":
            # added by michealxu
            #print "ggggget ready"
            for i in range(0,len(exec_string)): 
                if iscon[i] == -1:
                    debug = 1
                    __msg =  None
                    issucceedornot = False
                    try:
                        __tdw.init_set_use_Info(reExeNeedUse,reExeNeedSet)
                        __tdw.cli = client
                        module[i].TDW_PL(__tdw,argvlist[i][1:])
                        __msg = "TDW-00000 SUCCEED (session: %s python file: %s)" %(__tdw.get_session(), argvlist[i][0])
                        print "TDW-00000 SUCCEED (session: %s python file: %s)" %(__tdw.get_session(), argvlist[i][0])	
                        issucceedornot = True  
                        drop = 0
                    except HiveServerException, hsx:
                        __msg = "TDW-00105 FAILED: TDWServer run SQL error (session: %s python file: %s) (%s)" %(__tdw.get_session(), argvlist[i][0], hsx.message)
                        print "TDW-00105 FAILED: TDWServer run SQL error (session: %s python file: %s) (%s)" %(__tdw.get_session(), argvlist[i][0], hsx.message)
                        drop = 0
                        exitid = 105
                    except Thrift.TException, tx:
                        __msg = "TDW-00201 FAILED: Network communication error (session: %s python file: %s) (%s)" %(__tdw.get_session(), argvlist[i][0], tx.message)
                        print "TDW-00201 FAILED: Network communication error (session: %s python file: %s) (%s)" %(__tdw.get_session(), argvlist[i][0], tx.message) 
                        drop = 0
                        exitid = 201
                    except IOError, ie:
                        __msg = "TDW-00105 FAILED: TDWServer run SQL error (session: %s python file: %s) (%s)" % (__tdw.get_session(), argvlist[i][0], ie.strerror)
                        print "TDW-00105 FAILED: TDWServer run SQL error (session: %s python file: %s) (%s)" % (__tdw.get_session(), argvlist[i][0], ie.strerror)
                        drop = 0
                        exitid = 105						
                    except:
                        drop = 0	
                        exitid = 003
                        exc_type, exc_value, exc_traceback = sys.exc_info()
                        print "TDW-00003 FAILED: TDWClient run python error (session: %s python file: %s) : " %(__tdw.get_session(), argvlist[i][0])
                        __msg = getExceptTrackInfo()
                        traceback.print_exception(exc_type, exc_value, exc_traceback,limit=2, file=sys.stderr) 
                        
                    session = __tdw.get_session()
                    authid = __tdw.get_authid()
                    client = __tdw.cli
                    
                    if  issucceedornot == True :
                        __tdw.WriteLog("(%s)" % (__msg))
                    else:
                        if __tdw:
                            if __msg and __tdw.mark and __tdw.mark.logger:
                                __tdw.WriteLog("Error occured while executing user script:(%s)" % (__msg))
                            if __tdw.mark and __tdw.mark.logger: 
                                __tdw.finishMark()
                        if drop == 0:
                            sessiontodrop = __tdw.get_session()
                            try:
                                client.dropSession(sessiontodrop,__tdw.get_authid())
                                #print "drop: %s" %(sessiontodrop)
                            except:
                                drop = 0
                                #print "drop session failed: %s "  %(sessiontodrop)  
                        transport.close()
                        return exitid                            
                else:
                    if exec_string[i].strip() != "":
                        try:
                            strstr = exec_string[i]
                            if(len(strstr)>100):
                                strstr =  strstr[0:79] + "..." + strstr[len(strstr)-20:len(strstr)]                             
                            #res = client.execute(exec_string[i])
                            #added by michealxu
                            reExeNeedUse = __tdw.get_use_Info()
                            reExeNeedSet = __tdw.get_set_Info()
                            #print exec_string[i]
                            exec_string[i] = exec_string[i].strip()
                            tmpResult = exec_string[i].split(" ")
                            #print tmpResult[0].lower()
                            if tmpResult[0].lower() == "use" :
                                reExeNeedUse = exec_string[i]
                                #print exec_string[i]
                            elif tmpResult[0].lower() == "set" :
                                reExeNeedSet.append(exec_string[i])
                                #print exec_string[i]
                            tmpResult = exec_string[i].split("\t")
                            print tmpResult[0].lower()
                            if tmpResult[0].lower() == "use" :
                                reExeNeedUse = exec_string[i]
                                #print exec_string[i]
                            elif tmpResult[0].lower() == "set" :
                                reExeNeedSet.append(exec_string[i])
                                #print exec_string[i]
                            __tdw.init_set_use_Info(reExeNeedUse,reExeNeedSet)
                            
                            indexOfRetrytime2 = 0
                            retrytime2 = int(retrytime)
                            waittime22 = int(waittime2)
                            #print "???  %d  %d"  %(retrytime2,waittime22)
                            if retrytime2 == 0:
                                res = client.execute(exec_string[i])
                            else:
                                try:
                                    res = client.execute(exec_string[i])
                                    indexOfRetrytime2 = retrytime2
                                except Thrift.TException, tx:
                                    #try:
                                    #    client.dropSession(session, authid)
                                    #except Exception, ee:
                                    #    print "dropSession failed: %s"  %(session)
                                    #print "drop: %s" %(session)
                                    print "first time execute failed: %s"  %(tx.message)                                     
                            
                            #added by cherry start
                            historyip = list()
                            historyip.append(__tdw.ip)
                            #added by cherry end							
                            							
                            while indexOfRetrytime2 < retrytime2-1:
                                try:
                                    transport.close()
                                    time.sleep(waittime22)
                                    #added by cherry start
                                    num = random.randint(0, ipcounter-1)
                                    __tdw.ip = iplist[num]
                                    while indexOfRetrytime2+1 < ipcounter and historyip.count(__tdw.ip) > 0:
                                        num = random.randint(0, ipcounter-1)
                                        __tdw.ip = iplist[num]									
                                    print "%d time retry execute connect to hive ip:%s"%(indexOfRetrytime2+1,__tdw.ip)
                                    historyip.append(__tdw.ip)
                                    transport = TSocket.TSocket(__tdw.ip, port)
                                    #added by cherry end
                                    #transport = TSocket.TSocket(server, port)
                                    transport = TTransport.TBufferedTransport(transport)
                                    protocol = TBinaryProtocol.TBinaryProtocol(transport)
                                    client = ThriftHive.Client(protocol)
                                    transport.open()
                                    client.audit(user, passwd, dbname)
                                    sname = client.createSession("")
                                    session = sname[0]
                                    #print "create: %s" %(session)
                                    authid = sname[1]                
                                    res = client.execute("set tdw.username="+user)
                                    if reExeNeedUse != None:
                                        #print "reExeNeedUse"
                                        #print reExeNeedUse
                                        res = client.execute(reExeNeedUse)
                                    if len(reExeNeedSet) > 0:
                                        for oneset in reExeNeedSet:
                                            #print "oneset"
                                            #print oneset
                                            res = client.execute(oneset)                                    
                                    res = client.execute(exec_string[i])
                                    indexOfRetrytime2 = retrytime2
                                    #client.dropSession(session, authid)
                                    #session=None
                                    #authid=None                 
                                    #print "drop: %s" %(session)           
                                except Thrift.TException, tx:
                                    #try:
                                    #    client.dropSession(session, authid)
                                    #except Exception, ee:
                                    #    print "dropSession failed: %s"  %(session) 
                                    #print "drop: %s" %(session)
                                    indexOfRetrytime2 = indexOfRetrytime2 + 1;
                                    info = sys.exc_info()
                                    print "%d time retry execute failed: %s"  %(indexOfRetrytime2,tx.message) 
                            
                            if (indexOfRetrytime2 < retrytime2):
                                transport.close()
                                time.sleep(waittime22)
                                #added by cherry start
                                num = random.randint(0, ipcounter-1)
                                __tdw.ip = iplist[num]
                                while indexOfRetrytime2+1 < ipcounter and historyip.count(__tdw.ip) > 0:
                                    num = random.randint(0, ipcounter-1)
                                    __tdw.ip = iplist[num]	
                                print "%d time retry execute connect to hive ip:%s"%(indexOfRetrytime2+1,__tdw.ip)
                                transport = TSocket.TSocket(__tdw.ip, port)
                                #added by cherry end
                                #transport = TSocket.TSocket(server, port)
                                transport = TTransport.TBufferedTransport(transport)
                                protocol = TBinaryProtocol.TBinaryProtocol(transport)
                                client = ThriftHive.Client(protocol)
                                transport.open()
                                client.audit(user, passwd, dbname)
                                sname = client.createSession("")
                                session = sname[0]
                                #print "create: %s" %(session)
                                authid = sname[1]             
                                res = client.execute("set tdw.username="+user)
                                if reExeNeedUse != None:
                                    #print "reExeNeedUse"
                                    #print reExeNeedUse
                                    res = client.execute(reExeNeedUse)
                                if len(reExeNeedSet) > 0:
                                    for oneset in reExeNeedSet:
                                        #print "oneset"
                                        #print oneset
                                        res = client.execute(oneset)                                 
                                res = client.execute(exec_string[i])
                            #add end
                            if res == "success":
                                res = client.fetchAll()
                                printlist(res)
                                drop = 1
                                print "TDW-00000 SUCCEED ( session: %s query: %s )" %(session,strstr)							
                                #print "Execute '%s' replied ok w/ : " % (exec_string[i])
                        except HiveServerException, hsx:
                            print "TDW-00105 FAILED: TDWServer run SQL error (session: %s query: %s) (%s)" %(session,strstr,hsx.message)
                            drop = 1
                            exitid = 105
                            try:
                                client.dropSession(session, authid)
                            except Exception, ee:
                                drop = 1
                                #print "dropSession failed: %s"  %(session)
                            #print "drop: %s" %(session)                             
                            transport.close()
                            return exitid
                        except Thrift.TException, tx:
                            print "TDW-00201 FAILED: Network communication error (session: %s query: %s) (%s)" %(session,strstr,tx.message) 
                            drop = 1
                            exitid = 201
                            try:
                                client.dropSession(session, authid)
                            except Exception, ee:
                                drop = 1
                                #print "dropSession failed: %s"  %(session) 
                            #print "drop: %s" %(session)
                            transport.close()
                            return exitid
                        except:
                            drop = 1	
                            exitid = 105
                            print "TDW-00105 FAILED: TDWServer run SQL error: (session: %s query: %s)" %(session,strstr)
                            try:
                                client.dropSession(session, authid)
                            except Exception, ee:
                                drop = 1
                                #print "dropSession failed: %s"  %(session)    
                            #print "drop: %s" %(session)
                            transport.close()
                            return exitid
            # add end		
        elif action == "execf":
            debug = 1
            try:
                module.TDW_PL(tdw.TDW(client)) 
            except IOError, ie:
                print "Run TDW_PL error(%d) %s" % (ie.errno, ie.strerror)
                drop = 1
            # else:
            #     print "Unknown Error: ", sys.exc_info()[0]
        elif action == "put":
            try:
                tdw.TDW(client).uploadJob(fname + ".py")
                drop = 0
            except IOError, ie:
                print "Run TDW_PL error(%d) %s" % (ie.errno, ie.strerror)
                drop = 1

        if drop == 1:
            try:
                client.dropSession(session, authid)
            except :
                print "dropSession failed: %s"  %(session)    
            #print "drop: %s" %(session)
            #if check_result_int(res) != "ok":			
                #exitid = 103
                #print "TDW-00103 FAILED: TDWServer dropSession error (Drop Session %s failed)" %(session)		
                #print "Drop Session " + session + " " + check_result_int(res)		
        elif drop == 0:
            sessiontodrop = __tdw.get_session()
            authiddrop = __tdw.get_authid()
            try:
                client.dropSession(sessiontodrop,authiddrop)
            except:
                drop = 0
                print "drop session failed: %s"  %(sessiontodrop)  
            #print "drop: %s" %(sessiontodrop)
        #elif session != None:
        #    res = client.detachSession(session, authid)
        #    if check_result_int(res) != "ok":
        #        exitid = 104
        #        print "TDW-00104 FAILED: TDWServer detachSession error (Detach Session %s failed)" %(session)		 
        transport.close()
        return exitid

    except HiveServerException, hsx:
        print "TDW-00105 FAILED: TDWServer run SQL error (%s)" %(hsx.message)
        #print '%s' % (hsx.message)
        if debug == 1:
            pdb.set_trace()
        return 105     
    except Thrift.TException, tx:
        print "TDW-00201 FAILED: Network communication error (%s)" %(tx.message) 
        #print '%s' % (tx.message)
        if debug == 1:
            pdb.set_trace()
        return 201    
    except KeyboardInterrupt:
        print "TDW-00006 FAILED: keyboard interrupt"  
        return 6
    except SystemExit, se:
        print "Exit."
        return 0
    except:
        print "TDW-00255 FAILED: Unknown error: " 
        exc_type, exc_value, exc_traceback = sys.exc_info()
        traceback.print_exception(exc_type, exc_value, exc_traceback,
                                  limit=2, file=sys.stderr)
        return 255                        

def print_warning(session, string):
    '''Print session warning message'''
    if session == None:
        print "Warning, you do not supply any session info."
        print "Do you really want to " + string + " of a new session?"

def check_result(res):
    '''Check the return result'''
    if str(res) == "":
        return "failed"
    else:
        return "ok"

def check_result_int(res):
    '''Check the int result'''
    if int(res) == 0:
        return "ok"
    else:
        return "failed"

def printlist(res):
    '''Print a list'''
    if type(res) == type(list()):
        for a in res:
            print a
    elif type(res) == type(str()):
        print res

def printlist_self(res, sid, bc):
    '''Print a list with more arguments'''
    if type(res) == type(list()):
        for a in res:
            if sid != None and sid in a:
                bc.print_ok(a)
            else:
                print a

def tdwpl_compile():
    '''Noop'''
    print "Beging compiling ... (NOOP)"
    return None

class tdwpl_async(Thread):
    '''TDWPL_ASYNC class, however, you can ignore me:)'''
    def __init__(self, string, ti):
        Thread.__init__(self)
        self.string = string
        self.ti = ti
        self.status = 1
    def do_exit(self):
        '''Exit'''
        self.status = 0
    def run(self):
        while self.status:
            time.sleep(self.ti)
            if self.status:
                print self.name + ":" + self.string
            # do something else

class tdwpl_shell(cmd.Cmd):
    '''I am the superwoman, XiXiXi...
    TDWPL_SHELL class supply the command line environment for users.
    '''
    user = None
    passwd = None
    dbname = "default_db"
    server = None
    port = None
    client = None
    transport = None
    protocol = None
    session = None
    authid = None
    debug = 0
    __tdw = None
    bc = None
    errline = None
    autoedmode = True
    multiline = ""
    multiline_cmd = False
    editor = "emacs"
    prompt_prefix = "@TDW-PL$ "
    prompt_ml = "@>>>>>>> "
    prompt = "Dr.Who@TDW-PL$ "
    config = None
    jobid = -1
    autosaveconf = False
    savepasswd = False
    user_home = "~/.tdwpl"
    keywords = ["EOF", "color", "connect", "disconnect", "edit",
                "get", "passwd", "quit", "user", "attach", 
                "compile", "new", "n", "doc", "editor", "kill",
                "plc", "reset", "shows", "cat", "config", "detach",
                "discard", "exec", "ls", "put", "run", "status",
                "getschema", "getqp", "getqueryplan", "serverstatus",
                "clusterstatus", "gethistory", "host", "ed", "autoed",
                "exit", "start", "sleep", "server", "dbname", "port",
                "autosaveconf", "save", "c", "a", "d", "q", "savepasswd",
                "lsm","uploadm", "downloadm", "co", "ci", "sethome", 
                "upload", "makejar",
                "lsu", "cl", "lsl", 
                "help", "test"]

    def __init__(self, nogetpass = None, server = None, port = None, 
                 user = None, passwd = None):
        cmd.Cmd.__init__(self)
        cmd.Cmd.use_rawinput = use_readline
        self.bc = bcolors()

        # read in the user input
        print ("Got config from cmd line: mode %s connect to %s:%s@%s:%s" 
               % (str(nogetpass), str(server), str(port), 
                  str(user), str(passwd)))
        if server != None and server != "":
            self.server = str(server)
        if port != None and port != "":
            self.port = str(port)
        if user != None and user != "":
            self.user = str(user)
        if passwd != None and passwd != "":
            self.passwd = str(passwd)

        # read the config file now
        if nogetpass == 'x':
            self.pre_config()

        if (self.user == None or self.user == "" 
            or self.passwd == None or self.passwd == ""):
            getpass_flag = True
        else:
            getpass_flag = False

        # act on the previous results
        if getpass_flag:
            try:
                if nogetpass == 'm':
                    self.user = raw_input('Username: ')
                    self.prompt = self.user + self.prompt_prefix
                    self.passwd = getpass.getpass()
                elif nogetpass == 'x':
                    # the passwd has been loaded from the config file
                    if self.user == None:
                        self.user = raw_input('Username: ')
                    self.prompt = self.user + self.prompt_prefix
                    if self.passwd == None:
                        self.passwd = getpass.getpass()
                else:
                    self.user = raw_input('Username: ')
                    self.passwd = raw_input('Password: ')
                    self.prompt = ""
            except:
                print "Get password failed."
                sys.exit()
        else:
            self.prompt = self.user + self.prompt_prefix

        self.bc.print_pink("Use 'connect/c' to connect the server!")

        self.do_status("")

    def pre_config(self):
        '''PRE-config, we try to load the configurations from config file'''
        self.config = ConfigParser.ConfigParser()
        try:
            self.config.read(['/etc/tdwpl.conf', 
                              os.path.expanduser('~/.tdwpl.conf')])
        except (ConfigParser.NoSectionError, ConfigParser.NoOptionError), ne:
            # Ignore the exceptions
            if self.debug:
                print "Loading config file w/ err: '%s'" % (ne.message)
        try:
            self.user = self.config.get('section', 'user')
        except (ConfigParser.NoSectionError, ConfigParser.NoOptionError), ne:
            # Ignore the exceptions
            if self.debug:
                print "Loading config file w/ err: '%s'" % (ne.message)
        try:
            self.passwd = self.config.get('section', 'passwd')
        except (ConfigParser.NoSectionError, ConfigParser.NoOptionError), ne:
            # Ignore the exceptions
            if self.debug:
                print "Loading config file w/ err: '%s'" % (ne.message)
        try:
            self.server = self.config.get('section', 'server')
        except (ConfigParser.NoSectionError, ConfigParser.NoOptionError), ne:
            # Ignore the exceptions
            if self.debug:
                print "Loading config file w/ err: '%s'" % (ne.message)
        try:
            self.port = self.config.get('section', 'port')
        except (ConfigParser.NoSectionError, ConfigParser.NoOptionError), ne:
            # Ignore the exceptions
            if self.debug:
                print "Loading config file w/ err: '%s'" % (ne.message)
        try:
            self.dbname = self.config.get('section', 'dbname')
        except (ConfigParser.NoSectionError, ConfigParser.NoOptionError), ne:
            # Ignore the exceptions
            if self.debug:
                print "Loading config file w/ err: '%s'" % (ne.message)
        try:
            self.editor = self.config.get('section', 'editor')
        except (ConfigParser.NoSectionError, ConfigParser.NoOptionError), ne:
            # Ignore the exceptions
            if self.debug:
                print "Loading config file w/ err: '%s'" % (ne.message)
        try:
            self.autosaveconf = self.config.getboolean('section', 
                                                       'autosaveconf')
        except (ConfigParser.NoSectionError, ConfigParser.NoOptionError), ne:
            # Ignore the exceptions
            if self.debug:
                print "Loading config file w/ err: '%s'" % (ne.message)
        try:
            self.savepasswd = self.config.getboolean('section', 'savepasswd')
        except (ConfigParser.NoSectionError, ConfigParser.NoOptionError), ne:
            # Ignore the exceptions
            if self.debug:
                print "Loading config file w/ err: '%s'" % (ne.message)

    def post_config(self):
        '''POST-config, we write the config from memory to disk'''
        self.config = ConfigParser.RawConfigParser()

        self.config.add_section('section')
        if self.server != None:
            self.config.set('section', 'server', self.server)
        if self.port != None:
            self.config.set('section', 'port', self.port)

        if self.savepasswd:
            self.config.set('section', 'user', self.user)
            self.config.set('section', 'passwd', self.passwd)
        self.config.set('section', 'dbname', self.dbname)
        self.config.set('section', 'editor', self.editor)
        self.config.set('section', 'autosaveconf', self.autosaveconf)
        self.config.set('section', 'savepasswd', self.savepasswd)
        
        print "Writing the config file ..."
        try:
            f = open(os.path.expanduser('~/.tdwpl.conf'), "wb")
            self.config.write(f)
            f.close()
        except IOError, ioe:
            print "IOError on file '~/.tdwpl.conf':%d %s" % (ioe.errno, 
                                                             ioe.strerror)
            #print "TDW-00007 FAILED: TDWClient write config file error (~/.tdwpl.conf': %d %s)" % (ioe.errno,ioe.strerror)

    def do_save(self, line):
        """Save the current status to config file"""
        self.post_config()

    def sigint_handler(self, signo, frame):
        '''SIGINT handler'''
        print "Receiving SIGINT, trying to kill the running query"
        if self.__tdw != None:
            print "Killing ..."
            self.__tdw.do_exit()

    def autoed(self, fname, linenr):
        '''Toggle the auto editor mode'''
        if self.autoedmode == False:
            return
        if linenr == None:
            return
        line = "%s +%d %s" % (self.editor, linenr, fname)
        try:
            p = subprocess.Popen(line, close_fds=True, shell=True)
            p.wait()
        except OSError, ose:
            print "OSError %d %s" % (ose.errno, ose.strerror)

    def emptyline(self):
        '''Act on a emptyline, just return'''
        return

    def do_doc(self, line):
        """Print the document"""
        print "TDW-PL Shell Documentation, commands avaiable:"
        print bcolors.WARNING + "EOF attach config connect create "
        print "detach disconnect drop get kill"
        print "passwd put quit run show ls user doc"
        print bcolors.ENDC + ""
        print "Steps to using with the TDW:"
        print bcolors.OKPINK + "1. 'connect' to a TDW server." + bcolors.ENDC
        print "    You should provide the username, password."
        print bcolors.OKPINK + "2. 'new' or 'attach' to session." + bcolors.ENDC
        print ("    If 'attach' you should provide the session name "
               "and the svid.")
        print bcolors.OKPINK + "3. 'run' a job file." + bcolors.ENDC
        print "    You should provide the job file name."
        print (bcolors.OKPINK + 
               "4. 'put' a job file to TDW server and run it on the server." 
               + bcolors.ENDC)
        print "    You should provide the job file name."
        print bcolors.OKPINK + "5. 'get' a job's status." + bcolors.ENDC
        print "    You should provide the jobid, or default to the last job."
        print bcolors.OKPINK + "6. 'config' a job." + bcolors.ENDC
        print ("    You should provide the config string, "
               "refer the 'help config'.")
        print bcolors.OKPINK + "7. 'exec' a sql string." + bcolors.ENDC
        print "    You should provide the SQL string."
        print bcolors.OKPINK + "8. 'detach/discard' a session" + bcolors.ENDC
        print (bcolors.OKPINK + 
               "9. 'disconnect' from a TDW server." + 
               bcolors.ENDC)

    def do_user(self, line):
        """Set the user name
        user username"""
        if line == "":
            print self.user
        else:
            if self.client != None or self.session != None:
                print ("You can't change user in an active connection, "
                       "please disconnect it.")
                return
            self.user = line
            self.prompt = self.user + self.prompt_prefix
            print ("You have changed username to '%s', please reconnect"
                   "('disconnect' and 'c') to apply it." % (self.user))

    def do_passwd(self, line):
        """Set the user password"""
        try:
            self.passwd = getpass.getpass()
        except getpass.GetPassWarning, e:
            print "Get password failed."

    def do_sethome(self, line):
        """Set the home directory, default to '~/tdwpl'
        sethome path/to/home"""
        l = shlex.split(line)
        if len(l) >= 1:
            self.user_home = l[0]
        else:
            self.bc.print_err("Invalid arguments. See 'help sethome'.")
            return

    def do_c(self, line):
        """Connect to the server"""
        return self.do_connect(line)

    def do_connect(self, line):
        """Connect to the server"""
        if self.user == None or self.passwd == None:
            self.bc.print_err("TDW-00004 FAILED: TDWClient input arguments error (You have not provided your username and password)")
            #self.bc.print_err("Use 'user' and 'passwd' to set them.")
            self.bc.print_err("TDW-00004 FAILED: TDWClient input arguments error (Use 'user' and 'passwd' to set them)")
            return
        if self.server == None or self.port == None:
            #self.bc.print_err("You have not provided your server address or port.")
            #self.bc.print_err("Use 'server' and 'port' to set them.")
            self.bc.print_err("TDW-00004 FAILED: TDWClient input arguments error (You have not provided your server address or port)")
            self.bc.print_err("TDW-00004 FAILED: TDWClient input arguments error (Use 'server' and 'port' to set them)")
            return
        if self.client != None:
            #self.bc.print_err("Your have connected to a server.")
            self.bc.print_err("TDW-00001 FAILED: TDWClient connect server error (You have connected to a server)")
            return

        #add by cherry start
        try:
            resultlist = socket.getaddrinfo(self.server, None)
            ipcounter = 0
            iplist = [i[4][0] for i in resultlist]
            iplist = list(set(iplist))
            #for i in iplist:
            #    print "%s"%(i)
            ipcounter = len(iplist)
        except socket.error , gaie:
            print "TDW-00001 FAILED: TDWClient connect server %s error: %s"%(self.server, gaie[1])
            write_except_msg(__tdw, "TDW-00001 FAILED: TDWClient connect server %s error: %s"%(self.server, gaie[1]))
            exc_type, exc_value, exc_traceback = sys.exc_info()
            return 1
        #add by cherry end
		
        try:
            #add by cherry start
            num = random.randint(0, ipcounter-1)
            ip = iplist[num]
            print "connect to hive ip:%s"%(ip)
            self.transport = TSocket.TSocket(ip, int(self.port))
            #add by cherry end
            #self.transport = TSocket.TSocket(self.server, int(self.port))
            self.transport = TTransport.TBufferedTransport(self.transport)
            self.protocol = TBinaryProtocol.TBinaryProtocol(self.transport)
            self.client = ThriftHive.Client(self.protocol)
            self.transport.open()
        
            # Note that: the password should be transfered using 
            #            MD5 sum not the plain text.
            # pwdmd5 = hashlib.md5()
            # pwdmd5.update(self.passwd)
            # pwdmd5.digest()
            res = self.client.audit(self.user, self.passwd, self.dbname)
            if check_result_int(res) == "failed":
                self.bc.print_err("TDW-00100 FAILED: TDWServer audit user error: ")
                self.bc.print_err("Audit failed, please verify your settings.")
                self.bc.print_err("You maybe input a BAD "
                                  "user name or BAD password.")
                self.bc.print_err("Use 'user' and 'passwd' to "
                                  "change your profile :)")
                self.transport.close()
                self.client = None
                return
            self.bc.print_ok("Connect to server '" + self.server + ":" 
                             + str(self.port) + "' success.")
        except HiveServerException, hsx:
            print "TDW-00100 FAILED: TDWServer audit user error (%s)"  %(hsx.message)
            #print '%s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
            self.client = None
        except Thrift.TException, tx:
            print "TDW-00201 FAILED: Network communication error: "   
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            print "TDW-00001 FAILED: TDWClient connect server error: "
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_disconnect(self, line):
        """Disconnect from the server"""
        if self.client == None:
            self.bc.print_err("You have not connected to any server.")
            #self.bc.print_err("TDW-00001 FAILED: TDWClient connect server error (You have not connected to a server)")
            return
        try:
            if self.session != None:
                self.do_detach(line)
            self.transport.close()
        except HiveServerException, hsx:
            #print '%s' % (hsx.message)
            print "TDW-00104 FAILED: TDWServer detachSession error (%s)"  %(hsx.message)
            if self.debug == 1:
                pdb.set_trace()
            self.session = None
        except Thrift.TException, tx:
            print "TDW-00201 FAILED: Network communication error: "  
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            print "TDW-00104 FAILED: TDWServer detachSession error: " 
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

        self.client = None
        print "We have disconnected from the server '" + self.server + "'"
        
    def do_ls(self, line):
        """Show the sessions on the server"""
        return self.do_shows(line)
        
    def do_shows(self, line):
        """Show the sessions on the server"""
        if self.client == None:
            print_notconnected()
            return
        try:
            res = self.client.showSessions()
            self.bc.print_pink("Session List: ->")
            printlist_self(res, self.session, self.bc)
        except HiveServerException, hsx:
            print '%s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_n(self, line):
        """Create a new session"""
        return self.do_new(line)

    def do_new(self, line):
        """Create a new session"""
        if self.client == None:
            print_notconnected()
            return
        if self.session != None:
            print "Your have attached to a session."
            print "Use 'status' to get the client state."
            return

        l = shlex.split(line)
        if len(l) >= 1:
            session_name = l[0]
        else:
            # random session name
            session_name = ""

        try:
            res = self.client.createSession(session_name)
            print "New Session " + " ".join(res)
            self.session = res[0]
            self.authid = res[1]
            # add by joeyli begin
            # auto run a set command
            res = self.client.execute("set tdw.username="+self.user)
            # add by joeyli end
        except HiveServerException, hsx:
            print "TDW-00102 FAILED: TDWServer createSession error (%s)" %(hsx.message)
            #print '%s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
            self.session = None
        except Thrift.TException, tx:
            print "TDW-00201 FAILED: Network communication error: "
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            print "TDW-00102 FAILED: TDWServer createSession error: "
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_a(self, line):
        """Attach to a session
        attach session [authid]"""
        return self.do_attach(line)

    def do_attach(self, line):
        """Attach to a session
        attach session authid"""
        if self.client == None:
            print_notconnected()
            return
        if self.session != None:
            print "Your have attached to a session."
            print "Use 'status' to get the client state."
            return

        l = shlex.split(line)
        if len(l) >= 2:
            self.session = l[0]
            self.authid = l[1]
        elif len(l) == 1:
            self.session = l[0]
            self.authid = "default_authid"
        else:
            print "Missing args: attach sesstion authid"
            return
        
        if self.session == None or self.authid == None:
            print "Invalid session or authid."
            return
        try:
            res = self.client.requireSession(self.session, self.authid)
            print "Require Session " + self.session + " " + check_result(res)
            if check_result(res) == "failed":
                self.session = None
                self.authid = None
        except HiveServerException, hsx:
            print "TDW-00101 FAILED: TDWServer requireSession error (%s)" %(hsx.message)
            #print '%s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
            self.session = None
        except Thrift.TException, tx:
            print "TDW-00201 FAILED: Network communication error: "
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            print "TDW-00101 FAILED: TDWServer requireSession error: "
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")
        # add by joeyli begin
        # auto run a set command
        res = self.client.execute("set tdw.username="+self.user)
        # add by joeyli end
        
    def do_discard(self, line):
        """Drop a connected session, it is the current session!
        drop"""
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        try:
            res = self.client.dropSession(self.session, self.authid)
            print "Drop Session " + self.session + " " + check_result_int(res)
            if check_result_int(res) == "failed":
                print ("Maybe another client has attached to it, "
                       "we just detach it!")
        except HiveServerException, hsx:
            print "TDW-00103 FAILED: TDWServer dropSession error (%s)" %(hsx.message)
            #print '%s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            print "TDW-00201 FAILED: Network communication error: "
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            print "TDW-00103 FAILED: TDWServer dropSession error: "
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

        self.session = None
        self.authid = None
        
    def do_d(self, line):
        """Detach a connected session, it is the current session!
        detach"""
        return self.do_detach(line)

    def do_detach(self, line):
        """Detach a connected session, it is the current session!
        detach"""
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        try:
            res = self.client.detachSession(self.session, self.authid)
            print "Detach Session " + self.session + " " + check_result_int(res)
        except HiveServerException, hsx:
            print "TDW-00104 FAILED: TDWServer detachSession error (%s)" %(hsx.message)
            #print 'HiveServer: %s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            print "TDW-00201 FAILED: Network communication error: "
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            print "TDW-00104 FAILED: TDWServer detachSession error: "
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

        self.session = None
        self.authid = None

    def do_put(self, line):
        """Put a job file to the server and run it immediately.
        put file_name"""
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        l = shlex.split(line)
        if len(l) >= 1:
            fname = l[0]
        try:
            tdw.TDW(self.client).uploadJob(fname)
        except IOError, ioe:
            print "Run TDW_PL error(%d): %s" % (ioe.errno, ioe.strerror)
        except HiveServerException, hsx:
            print '%s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_gethistory(self, line):
        """Get a job's history file content
        gethistory jobid"""
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        l = shlex.split(line)
        try:
            if len(l) >= 1:
                self.jobid = l[0]
            else:
                self.jobid = -1
            if int(self.jobid) < -1 or int(self.jobid) > sys.maxint:
                raise ValueError
            if self.jobid == "" or self.jobid == None:
                self.jobid = -1
        except ValueError, ve:
            print ("Invalid argument for jobid '%s': %s" 
                   % (str(l[0]), " should be a valid integer number!"))
            return
        except OverflowError, oe:
            print ("Number overflow for jobid '%s'" % l[0])
            return

        try:
            res = self.client.getHistory(int(self.jobid))
            print "getHistory (%d)->" % (int(self.jobid))
            printlist(res)
        except ValueError, ve:
            print ("Invalid argument for jobid '%s': should be number!" 
                   % str(self.jobid))
            self.jobid = -1
        except OverflowError, oe:
            print ("Number overflow for jobid '%s'. %s" % 
                   (str(self.jobid),
                    "Exception from Thrift, we have to disconnect from server!"))
            self.do_disconnect("")
        except HiveServerException, hsx:
            print '%s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_get(self, line):
        """Get a job's status
        get jobid"""
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        l = shlex.split(line)
        try:
            if len(l) >= 1:
                self.jobid = int(l[0])
            else:
                self.jobid = -1
            if int(self.jobid) < -1 or int(self.jobid) > sys.maxint:
                raise ValueError
            if self.jobid == "" or self.jobid == None:
                self.jobid = -1
        except ValueError, ve:
            print ("Invalid argument for jobid '%s': %s" 
                   % (str(l[0]), " should be a valid integer number!"))
            return
        except OverflowError, oe:
            print ("Number overflow for jobid '%s'" % l[0])
            return

        try:
            res = self.client.getJobStatus(int(self.jobid))
            print "getJobStatus (%d)->" % (int(self.jobid))
            printlist(res)
        except ValueError, ve:
            print ("Invalid argument for jobid '%s': should be a number!" 
                   % str(self.jobid))
            self.jobid = -1
        except OverflowError, oe:
            print ("Number overflow for jobid '%s'. %s" % 
                   (str(self.jobid),
                    "Exception from Thrift, we have to disconnect from server!"))
            self.do_disconnect("")
        except HiveServerException, hsx:
            print '%s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_compile(self, line):
        """Compile a job file
        compile file_name"""
        result = None
        l = shlex.split(line)
        if len(l) >= 1:
            fname = l[0]
        if fname == "":
            #print "Invalid file name. Usage: compile [filename]"
            print "TDW-00004 FAILED: TDWClient input arguments error (Invalid file name)"
            return False

        self.errline = None
        if self.client == None or self.session == None or self.authid == None:
            self.bc.print_warn("You have not connected or attached to any "
                               "server or session, we only do local "
                               "Python compile.")
            try:
                read_data = ""
                f = open(fname, "r")
                read_data = f.read()
                f.close()
            except IOError, ioe:
                #print "IOError on file '%s':%d %s" % (fname, ioe.errno, ioe.strerror)
                print "TDW-00005 FAILED: TDWClient python file read error ('%s':%d %s)" %(fname, ioe.errno, ioe.strerror)                                    
                return False

            try:
                self.bc.print_warn("Compiling file '%s' ... (Python Only)" 
                                   % (fname))
                compile(read_data, '<string>', 'exec')
            except SyntaxError, se:
                #print "SynaxError : %s '%s' Line %d Offset %d" % (se.filename, 
                #                                                  se.text,
                #                                                  se.lineno, 
                #                                                  se.offset)
                print "TDW-00002 FAILED: TDWClient compile python error (SynaxError : %s %s Line %d Offset %d)" %(se.filename,se.text,se.lineno,se.offset)	                                      
                self.errline = se.lineno
                return False
            except TypeError, te:
                #print "TypeError  : %s" % (te.strerror)
                print "TDW-00002 FAILED: TDWClient compile python error (TypeError : %s)"  %(te.strerror)
                return False
            return
        else:
            print "OK, compile the PL code with server."
            try:
                read_data = ""
                f = open(fname, "r")
                read_data = f.read()
                f.close()
            except IOError, ioe:
                #print "IOError on file '%s':%d %s" % (fname, ioe.errno, 
                #                                      ioe.strerror)
                print "TDW-00005 FAILED: TDWClient python file read error ('%s':%d %s)" %(fname, ioe.errno, ioe.strerror)                                      
                return False

            try:
                self.bc.print_warn("Compiling file '%s' ... (Python Only)" 
                                   % (fname))
                compile(read_data, '<string>', 'exec')
            except SyntaxError, se:
                #print "SynaxError : %s '%s' Line %d Offset %d" % (se.filename, 
                #                                                  se.text,
                #                                                  se.lineno, 
                #                                                  se.offset)
                print "TDW-00002 FAILED: TDWClient compile python error (SynaxError : %s %s Line %d Offset %d)" %(se.filename,se.text,se.lineno,se.offset)	                                                                                                                      
                self.errline = se.lineno
                return False
            except TypeError, te:
                #print "TypeError  : %s" % (te.strerror)
                print "TDW-00002 FAILED: TDWClient compile python error (TypeError : %s)"  %(te.strerror)
                return False

            self.bc.print_warn("Compiling file '%s' ... (SQL Only)" % (fname))
            end = 0
            linenr = 1
            while True:
                start = read_data.find("'''", end)
                if start < 0:
                    break
                for char in read_data[end:start]:
                    if char == '\n':
                        linenr = linenr + 1
                end = read_data.find("'''", start + 3)
                if end < 0:
                    break
                try:
                    res = self.client.compile(read_data[start + 3:end])
                    print ("Check SQL @L %6d: '%s' %s" 
                           % (linenr, read_data[start + 3:end], res))
                    if res != "success":
                        result = False
                        self.errline = linenr
                except HiveServerException, hsx:
                    #print ("Check SQL @L %6d: '%s' %s" 
                    #       % (linenr, read_data[start + 3:end], hsx.message))
                    print "TDW-00105 FAILED: TDWServer run SQL error (Check SQL @L %6d: '%s' %s)" % (linenr, read_data[start + 3:end], hsx.message)     
                    result = False
                    self.errline = linenr
                    if self.debug == 1:
                        pdb.set_trace()
                except Thrift.TException, tx:
                    print ("Check SQL @L %6d: '%s' %s" 
                           % (linenr, read_data[start + 3:end], tx.message))
                    print "TDW-00201 FAILED: Network communication error: "    
                    #print "TDW-00105 FAILED: TDWServer run SQL error (Check SQL @L %6d: '%s' %s)" % (linenr, read_data[start + 3:end], hsx.message)       
                    self.auto_disconnect('Thrift: %s' % (tx.message))
                    result = False
                    self.errline = linenr
                    if self.debug == 1:
                        pdb.set_trace()
                except:
                    print "TDW-00105 FAILED: TDWServer run SQL error: "
                    exc_type, exc_value, exc_traceback = sys.exc_info()
                    traceback.print_exception(exc_type, exc_value, 
                                              exc_traceback,
                                              limit=2, file=sys.stderr)
                    self.do_disconnect("")
                    result = False
                    self.errline = linenr
                    if self.debug == 1:
                        pdb.set_trace()

                end = end + 3

            return result

    def do_plc(self, line):
        """Compile a job file, with editor opened automatically
        compile file_name"""
        saved_line = line
        res = self.do_compile(line)
        if res == False and self.autoedmode == True:
            l = shlex.split(saved_line)
            if len(l) >= 1:
                fname = l[0]
            if fname == "":
                return False

            self.autoed(fname, self.errline)
            return False
        else:
            return res

    def do_cat(self, line):
        """Cat a job file
        cat filename"""
        l = shlex.split(line)
        if len(l) >= 1:
            fname = l[0]

        if fname == "":
            print "TDW-00005 FAILED: TDWClient python file read error (Invalid file name)"  
            #print "Invalid file name"
            return

        # OK, we should cat the file now
        try:
            read_data = ""
            f = open(fname, "r")
            read_data = f.read()
            f.close()
            if read_data != "":
                print read_data
        except IOError, ioe:
            print "TDW-00005 FAILED: TDWClient python file read error (Cat PL code error(%d): {%s})" % (ioe.errno, ioe.strerror)  
            #print "Cat PL code error(%d): {%s}" % (ioe.errno, ioe.strerror)

    def do_editor(self, line):
        """Set the editor to edit, the default editor is EMACS
        editor NAME"""
        l = shlex.split(line)
        if len(l) >= 1:
            editor = l[0]
        else:
            editor = ""

        if editor != "":
            self.editor = editor
        print "Change default Editor to " + self.editor
        
    def do_host(self, line):
        """Call the shell commands"""
        output = os.popen(line).read()
        print output
        self.last_output = output

    def do_ed(self, line):
        """Call the Editor to edit the file
        You can change your default editor by 'editor'."""
        return self.do_edit(line)

    def do_edit(self, line):
        """Call the Editor to edit the file
        You can change your default editor by 'editor'."""
        l = shlex.split(line)
        if len(l) >= 1:
            fname = l[0]
        else:
            fname = ""

        if fname == "":
            print "TDW-00005 FAILED: TDWClient python file read error (Invalid file name or arg name)"  
            #print "Invalid file name or arg name"
            return

        # OK, we should call the editor now
        line = self.editor
        for x in l:
            line = line + " " + x
            
        try:
            p = subprocess.Popen(line, close_fds=True, shell=True)
            p.wait()
        except OSError, ose:
            print "TDW-00202 FAILED: OS error (OSError %s)" % (ose.strerror)
            #print "OSError %s" % (ose.strerror)
        cmd.Cmd.last_output = "Edit OK."

    def do_start(self, line):
        """Run a job file in the interactive mode, the same as '@' 'run'
        run file_name"""
        return self.do_run(line)

    def do_run(self, line):
        """Run a job file in the interactive mode
        run file_name"""
        ms = []
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        l = shlex.split(line)
        if len(l) >= 1:
            fname = l[0]
        argv = l[1:]
        l = fname.rsplit("/")
        file_name = l[len(l) - 1]
        l = file_name.split(".")
        module_name = l[0]

        res = self.do_compile(fname)
        if res == False:
            print "Compiling failed, please fix your code."
            return
      
        if module_name == "":
            print "TDW-00005 FAILED: TDWClient python file read error (Invalid module name)"
            #print "Invalid module name (None)"
            return

        # OK, we should do file checking now
        try:
            mode, ms = tdw_file_checking(fname)
            # then, we create the local lib if not exists
            try:
                os.makedirs("%s/lib/%s" % 
                            (self.user_home.replace("~", 
                                                    os.getenv("HOME")),
                             self.user))
            except OSError, oe:
                # just ignore the error
                if oe.errno == errno.EEXIST:
                    pass
                else:
                    print "TDW-00202 FAILED: OS error (OSError %s)" % (oe.strerror)
                    raise oe

            if mode == "server":
                for m in ms:
                    if m == "":
                        continue
                    # change path m to posix path
                    m = m.replace(".", "/", m.count(".") - 1)
                    # make sure the subdir exists
                    if m.count("/"):
                        try:
                            os.makedirs("%s/lib/%s" 
                                        % (self.user_home.replace("~", 
                                                                  os.getenv("HOME")),
                                           m.split("/")[0]))
                        except OSError, oe:
                            if oe.errno == errno.EEXIST:
                                pass
                            else:
                                print "TDW-00202 FAILED: OS error (OSError %s)" % (oe.strerror)
                                raise oe

                    content = self.client.downloadModule(self.user, m)
                    if content == "":
                        self.bc.print_warn("Module %s is empty! "
                                           "Stop downloading..." 
                                           % m)
                        break
                    if m.count("/"):
                        mpath = ("%s/lib/%s" 
                                 % (self.user_home.replace("~",
                                                           os.getenv("HOME")),
                                    m))
                    else:
                        mpath = ("%s/lib/%s/%s" 
                                 % (self.user_home.replace("~", 
                                                           os.getenv("HOME")), 
                                    self.user, m))
                    f = open(mpath, "w")
                    f.write(content)
                    f.close()
                    self.bc.print_ok("Preparing module %s successful to '%s'" % 
                                     (m, mpath))
        except IOError, ioe:
            print "TDW-00005 FAILED: TDWClient python file read error (PL checking error(%d): %s)" % (ioe.errno, ioe.strerror)  
            #print "PL checking error(%d): %s" % (ioe.errno, ioe.strerror)
            return
        except HiveServerException, hsx:
            
            #print 'HiveServer: %s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
            return
        except Thrift.TException, tx:
            print "TDW-00201 FAILED: Network communication error: "  
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
            return
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")
            return

        sys.path.append(fname.rstrip(file_name))
        sys.path.append("%s/lib/" 
                        % (self.user_home.replace("~", 
                                                  os.getenv("HOME"))))
        sys.path.append("%s/lib/global" 
                        % (self.user_home.replace("~", 
                                                  os.getenv("HOME"))))
        sys.path.append("%s/lib/%s" 
                        % (self.user_home.replace("~", 
                                                  os.getenv("HOME")),
                           self.user))

        try:
            self.bc.print_pink("Module Name: " + module_name)
            self.bc.print_pink("You can use Ctrl+C to stop the job. ")
            self.bc.print_pink("The job will stop after completing "
                               "the current SQL query.")

            module = __import__(module_name)

            # Signal handler is not needed? for python 2.6 smooth handling
            # signal.signal(signal.SIGINT, self.sigint_handler)
            # signal.siginterrupt(signal.SIGINT, True)

            # Do we need this notifier?
            # string = "TDW PL is running, please just wait for the result or Ctrl+C to stop it."
            # t = tdwpl_async(string, 5)
            # t.start()

            self.__tdw = tdw.TDW(self.client)
            self.__tdw.init_mark(argv)
            module.TDW_PL(self.__tdw, argv)
            self.__tdw = None

            # t.do_exit()

            # signal.signal(signal.SIGINT, signal.SIG_DFL)

            try:
                del sys.modules[module_name]
                del module_name

                # clean the imported modules
                for m in ms:
                    if m == "":
                        continue
                    if m.lstrip("global.") == "tdw_sql_lib.py":
                        m = m.lstrip("global.")
                    del sys.modules[m.rstrip("py").rstrip(".")]
                    mname = m.rstrip("py").rstrip(".")
                    del mname
            except KeyError, ke:
                print "Delete module %s failed, can't find it!" % ke
                self.bc.print_err("Import some module for many times? "
                                  "I am confused and corrupted :(")
                self.bc.print_err("You should reset the client now!")
                pass

        except ImportError, ie:
            print "Loading module error: %s" % (ie)
            print "Howto fix it:"
            print ("""1. You can find the related modules in """
                   """local library: "User home" (CMD: 'status')""")
            print ("2. Check the local library: (CMD: '!ls %s/lib/')" 
                   % self.user_home)
            try:
                dpath = ("%s/lib/" 
                         % (self.user_home.replace("~", 
                                                   os.getenv("HOME"))) 
                         + str(ie).split(" ")[-1].split(".")[-2])
                print ("3. Make sure that there is a '__init__.py' "
                       "file in dir: %s" % (dpath))
                print "   CMD: '!touch %s/__init__.py'" % dpath
            except:
                pass
            print "F. Make sure the module file is in the following path:"
            for x in sys.path:
                print x
        except IOError, e:
            print "TDW-00003 FAILED: TDWClient run python error (IOError(%d): %s)"  %(e.errno, e.strerror)
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            #print "Run TDW_PL w/ IOError(%d): %s" % (e.errno, e.strerror)
            # stop the thread and reset the signal handler and 
            # unload the module
            self.__tdw = None
            # t.do_exit()
            # signal.signal(signal.SIGINT, signal.SIG_DFL)
            try:
                del sys.modules[module_name]
                del module_name

                # clean the imported modules
                for m in ms:
                    if m == "":
                        continue
                    if m.lstrip("global.") == "tdw_sql_lib.py":
                        m = m.lstrip("global.")
                    del sys.modules[m.rstrip("py").rstrip(".")]
                    mname = m.rstrip("py").rstrip(".")
                    del mname
            except KeyError, ke:
                print "Delete module %s failed, can't find it!" % ke
                self.bc.print_err("Import some module for many times? "
                                  "I am confused and corrupted :(")
                pass
            if e.errno == 4:              # EINTR
                self.bc.print_err("You interrupt the Thrift which currently do"
                                  " not handle EINTR properly.")
                self.bc.print_err("We will exit, and you should reconnect "
                                  "('c') and reattach ('a') to the "
                                  "current session.")
                sys.exit()
        except HiveServerException, hsx:
            print "TDW-00105 FAILED: TDWServer run SQL error: %s" %(hsx.message)
            #print 'HiveServer: %s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
            self.__tdw = None
            # signal.signal(signal.SIGINT, signal.SIG_DFL)
            try:
                del sys.modules[module_name]
                del module_name

                # clean the imported modules
                for m in ms:
                    if m == "":
                        continue
                    if m.lstrip("global.") == "tdw_sql_lib.py":
                        m = m.lstrip("global.")
                    del sys.modules[m.rstrip("py").rstrip(".")]
                    mname = m.rstrip("py").rstrip(".")
                    del mname
            except KeyError, ke:
                print "Delete module %s failed, can't find it!" % ke
                self.bc.print_err("Import some module for many times? "
                                  "I am confused and corrupted :(")
                pass
        except Thrift.TException, tx:
            print "TDW-00201 FAILED: Network communication error: "
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
            self.__tdw = None
            # signal.signal(signal.SIGINT, signal.SIG_DFL)
            try:
                del sys.modules[module_name]
                del module_name

                # clean the imported modules
                for m in ms:
                    if m == "":
                        continue
                    if m.lstrip("global.") == "tdw_sql_lib.py":
                        m = m.lstrip("global.")
                    del sys.modules[m.rstrip("py").rstrip(".")]
                    mname = m.rstrip("py").rstrip(".")
                    del mname
            except KeyError, ke:
                print "Delete module %s failed, can't find it!" % ke
                self.bc.print_err("Import some module for many times? "
                                  "I am confused and corrupted :(")
                pass
        except:
            print "TDW-00003 FAILED: TDWClient run python error: "
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            # stop the thread and reset the signal handler and 
            # unload the module
            self.__tdw = None
            # t.do_exit()
            # signal.signal(signal.SIGINT, signal.SIG_DFL)
            try:
                del sys.modules[module_name]
                del module_name

                # clean the imported modules
                for m in ms:
                    if m == "":
                        continue
                    if m.lstrip("global.") == "tdw_sql_lib.py":
                        m = m.lstrip("global.")
                    del sys.modules[m.rstrip("py").rstrip(".")]
                    mname = m.rstrip("py").rstrip(".")
                    del mname
            except KeyError, ke:
                print "Delete module %s failed, can't find it!" % ke
                self.bc.print_err("Import some module for many times? "
                                  "I am confused and corrupted :(")
                pass

        sys.path.remove(fname.rstrip(file_name))
        sys.path.remove("%s/lib/" 
                        % (self.user_home.replace("~", 
                                                  os.getenv("HOME"))))
        sys.path.remove("%s/lib/global" 
                        % (self.user_home.replace("~", 
                                                  os.getenv("HOME"))))
        sys.path.remove("%s/lib/%s" 
                        % (self.user_home.replace("~", 
                                                  os.getenv("HOME")),
                           self.user))

    def do_config(self, line):
        """Config this job:
        config config_string
               [type=ONESHOT/REPEAT;ti=<int>]
               ti is 'time interval' in second."""
        print "'config' command is obsolete, please do not use it now."
        return
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return

        l = shlex.split(line)
        if len(l) >= 1:
            config = l[0]
        if config == "":
            print "Empty config string."
            print "config format: [type=ONESHOT/REPEAT];[ti=%d]"
            return
        try:
            res = self.client.configJob(config)
            print "configJob " + check_result_int(res)
        except HiveServerException, hsx:
            print '%s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_kill(self, line):
        """Kill the running job."""
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return

        try:
            res = self.client.killJob()
            print "killJob " + check_result_int(res)
        except HiveServerException, hsx:
            print '%s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            print "TDW-00201 FAILED: Network communication error: "
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def onecmd(self, line):
        if line == None:
            if self.multiline_cmd:
                line = self.multiline
            else:
                return self.emptyline()
        else:
            line = line.strip()
            if line.endswith('\\'):
                if self.multiline_cmd:
                    self.multiline += line.rstrip('\\')
                else:
                    self.multiline_cmd = True
                    self.multiline = line.rstrip('\\')
                    self.prompt = self.user + self.prompt_ml
                return self.emptyline()
            else:
                if self.multiline_cmd:
                    self.multiline += line.rstrip('\\')
                    line = self.multiline
                    self.multiline_cmd = False
                    self.prompt = self.user + self.prompt_prefix
                
        if line != "" and line[0] == '/':
            line = self.lastcmd
        command, arg, line = self.sqlplus(line)
        if not line:
            return self.emptyline()
        if command is None:
            return cmd.Cmd.default(self, line)
        self.lastcmd = line
        if command == '':
            return cmd.Cmd.default(self, line)
        else:
            try:
                func = getattr(self, 'do_' + command)
            except AttributeError:
                return cmd.Cmd.default(self, line)
            return func(arg)

    def sqlplus(self, line):
        """Parse the line into a command name and a string containing the
        arguments.
        """
        line = line.strip()
        if not line:
            return None, None, line
        elif line[0] == '?':
            line = 'help ' + line[1:]
        elif line[0] == '@':
            line = 'run ' + line[1:]
        elif line[0] == '#':
            return None, None, None
        elif line[0] == '!':
            if hasattr(self, 'do_host'):
                line = 'host ' + line[1:]
            else:
                return None, None, line
        i, n = 0, len(line)
        while i < n and line[i] in self.identchars: 
            i = i + 1
        cmd, arg = line[:i], line[i:].strip()
        if cmd not in self.keywords:
            cmd = 'exec'
            arg = line
        return cmd, arg, line

    def do_exec(self, line):
        """Exec a sql statement."""
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        if line == None or line == "":
            print "TDW-00004 FAILED: TDWClient input arguments error (Empty query statement)"
            #print "Empty query statement."
            return
        # strip the line w/ ';'
        line = line.rstrip(';')
        
        try:
            res = self.client.execute(line)
            if res == "success":
                res = self.client.fetchAll()
            printlist(res) 
            strstr = line
            if(len(strstr)>100):
                strstr =  strstr[0:79] + "..." + strstr[len(strstr)-20:len(strstr)]                    
            print "TDW-00000 SUCCEED ( session: %s query: %s )" %(self.session, strstr)    
            #self.bc.print_pink("Execute '%s' replied ok w/ :" % (line))
        except HiveServerException, hsx:
            print "TDW-00105 FAILED: TDWServer run SQL error: "
            self.bc.print_err('HiveServer: %s' % (hsx.message))
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            print "TDW-00201 FAILED: Network communication error: "
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except KeyboardInterrupt:
            print "TDW-00006 FAILED: keyboard interrupt" 
            return 6 
        except:
            print "TDW-00105 FAILED: TDWServer run SQL error: "
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_serverstatus(self, line):
        """Get the Server status. This function is useless now"""
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return

        try:
            res = self.client.getStatus()
            self.bc.print_pink("serverStatus replied ok w/ :")
            print res
        except HiveServerException, hsx:
            print 'HiveServer: %s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")
        
    def do_getqueryplan(self, line):
        """Get the Query Plan of the last SQL query."""
        return self.do_getqp(line)

    def do_getqp(self, line):
        """Get the Query Plan of the last SQL query."""
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return

        print "Get Query Plan is not support now."
        print "Please ask TDW to update Hive to higher version."
        return

        '''
        try:
            res = self.client.getQueryPlan()
            self.bc.print_pink("getQueryPlan replied w/ :")
            print res
        except HiveServerException, hsx:
            print 'HiveServer: %s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")
        '''

    def do_clusterstatus(self, line):
        """Get the Cluster Status."""
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return

        try:
            res = self.client.getClusterStatus()
            self.bc.print_pink("clusterStatus replied w/ :")
            print res
        except HiveServerException, hsx:
            print 'HiveServer: %s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_getschema(self, line):
        """Get the Schema of the last SQL query."""
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return

        try:
            res = self.client.getSchema()
            self.bc.print_pink("getSchema replied w/ :")
            print res
        except HiveServerException, hsx:
            print 'HiveServer: %s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_lsm(self, line):
        """List the library modules in [user]'s directory
        lsm [user]
        """
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        l = shlex.split(line)
        if len(l) >= 1:
            username = l[0]
            if username == "":
                username = self.user
        else:
            username = self.user

        try:
            modules = self.client.listModule(username)
            # print the modules now
            self.bc.print_pink("Server Library for %s:" % username)
            printlist(modules)
        except HiveServerException, hsx:
            print 'HiveServer: %s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_lsu(self, line):
        """List the active users in the server-side library
        lsu
        """
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return

        try:
            modules = self.client.listModule("")
            # print the modules now
            self.bc.print_pink("Active Users in Server Library:")
            printlist(modules)
        except HiveServerException, hsx:
            print 'HiveServer: %s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_uploadm(self, line):
        """Upload a module to user's library
        uploadm module_pathname [user]
        For example: uploadm /path/to/bicdm.py [bicdm]
        """
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        l = shlex.split(line)
        if len(l) >= 1:
            module_pathname = l[0]
            # parse the module_name
            module_name = os.path.basename(module_pathname)
            if module_name == "":
                print "Invalid pathname, should be a python file."
                return
            if len(l) == 2:
                username = l[1]
            else:
                username = self.user
        else:
            print "Invalid arguments."
            print "uploadm module_name user module_pathname"
            print "For example: uploadm /path/to/bicdm.py [username]"
            return

        read_data = ""
        try:
            f = open(module_pathname, "r")
            read_data = f.read()
            f.close()
            if read_data == "":
                delete = raw_input("Do you want to delete %s's module '%s' "
                                   "on server? [Y/N] " % (username,
                                                          module_name))
                if delete == "n" or delete == "N" or delete == "":
                    print ("Read file '" + module_pathname + 
                           "' failed, zero length.")
                    return
            res = self.client.uploadModule(username, module_name, read_data)
            if res == "ok":
                self.bc.print_ok("Upload module %s successful." % module_name)
        except IOError, ie:
            print "Read module file err(%d) %s" % (ie.errno, ie.strerror)
            return
        except HiveServerException, hsx:
            self.bc.print_err('%s' % (hsx.message))
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            print '%s' % (tx.message)
            if self.debug == 1:
                pdb.set_trace()

    def do_downloadm(self, line):
        """Download a module to user's local directory
        downloadm module_name user module_pathname_to_save
        For example: downloadm bicdm.py bicdm /path/to/local_bicdm.py
        """
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        l = shlex.split(line)
        if len(l) >= 3:
            module_name = l[0]
            username = l[1]
            module_pathname = l[2]
        else:
            print "Invalid arguments."
            print "downloadm module_name user module_pathname_to_save"
            print "For example: downloadm bicdm.py bicdm /path/to/local_bicdm.py"
            return
        
        try:
            content = self.client.downloadModule(username, module_name)
            if content == "":
                self.bc.print_warn("Module %s is empty! Abort downloading..." 
                                   % module_name)
            f = open(module_pathname, "w")
            f.write(content)
            f.close()
            self.bc.print_ok("Download module %s successful to '%s'" % 
                             (module_name, module_pathname))
        except IOError, ie:
            print "Write module file err(%d) %s" % (ie.errno, ie.strerror)
            return
        except HiveServerException, hsx:
            self.bc.print_err('%s' % (hsx.message))
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            print '%s' % (tx.message)
            if self.debug == 1:
                pdb.set_trace()

    def do_upload(self, line):
        """Upload file to hiveserver
        upload file_type file_pathname [user]
        For example: upload jar /path/to/db_table1.jar [username]
                   : upload proto /path/to/table1.proto [username]
        """
        resource_type = ["proto","jar"]
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        l = shlex.split(line)
        if len(l) >= 2:
            rtype = l[0].lower()
            if rtype in resource_type:
                file_pathname = l[1]
                # parse the file_name
                file_name = os.path.basename(file_pathname)
                if file_name == "":
                    print "Invalid pathname, should be a file."
                    return
                if len(l) >= 3:
                    username = l[2]
                else:
                    username = self.user
            else:
		print "Invalid arguments."
		print "upload [%s] file_pathname ..." %("|".join(resource_type))
		print "For example: upload jar /path/to/db_table1.jar [username]"
		print "           : upload proto /path/to/table1.proto [username]"
		return
        else:
            print "Invalid arguments."
            print "upload [%s] file_pathname ..." %("|".join(resource_type))
            print "For example: upload jar /path/to/db_table1.jar [username]"
            print "           : upload proto /path/to/table1.proto [username]"
            return

        read_data = ""
        try:
            f = open(file_pathname, "r")
            read_data = f.read()
            f.close()
            if read_data == "":
                delete = raw_input("Do you want to delete %s's file '%s' "
                                   "on server? [Y/N] " % (username,
                                                          file_name))
                if delete == "n" or delete == "N" or delete == "":
                    print ("Read file '" + file_pathname + 
                           "' failed, zero length.")
                    return
	    res = self.client.upload(rtype, username, file_name, read_data)

            if res == "ok":
                self.bc.print_ok("Upload file %s successful." % file_name)
        except IOError, ie:
            print "Read  file err (%d) %s" % (ie.errno, ie.strerror)
            return
        except HiveServerException, hsx:
            self.bc.print_err('%s' % (hsx.message))
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            print '%s' % (tx.message)
            if self.debug == 1:
                pdb.set_trace()

    def do_makejar(self, line):
        """Preprocess proto file & create jar package for create table.
        Usage: makejar dbname tablename [filename] [username]
        """
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        l = shlex.split(line)
        if len(l) == 4:
            dbname,tablename,filename,username = l[0],l[1],l[2],l[3]
        elif len(l) == 3:
            dbname,tablename,filename,username = l[0],l[1],l[2],self.user
        elif len(l) == 2:
            dbname,tablename,filename,username = l[0],l[1],l[1]+'.proto',self.user
        else:
            print """Preprocess proto file & create jar package for creating table.
	    Usage: makejar dbname tablename [filename] [username]
                  """
            return
        # firstly, we try to preprocess proto file for creating table 
        try:
            res = self.client.makejar(dbname,tablename,filename,username)
	    self.bc.print_ok("makejar for [%s.%s] successful to: %s" %(dbname,tablename,res))
        except HiveServerException, hsx:
            print 'HiveServer: %s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except OSError, oe:
            self.bc.print_err("OSError: %s" % oe)
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_co(self, line):
        """Checkout the user's library to local lib.
        co [user]
        """
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        l = shlex.split(line)
        if len(l) >= 1:
            username = l[0]
            if username == "":
                username = self.user
        else:
            username = self.user

        # firstly, we try to get the lib content
        try:
            modules = self.client.listModule(username)
            # then, we create the local lib if not exists
            try:
                os.makedirs("%s/lib/%s" 
                            % (self.user_home.replace("~", 
                                                      os.getenv("HOME")),
                               username))
            except OSError, oe:
                # just ignore the error
                if oe.errno == errno.EEXIST:
                    pass
                else:
                    raise oe

            # finally, we download each file to the local lib
            for m in modules:
                if m == "":
                    continue
                # change path m to posix path
                m = m.replace(".", "/", m.count(".") - 1)
                content = self.client.downloadModule(username, m)
                if content == "":
                    self.bc.print_warn("Module %s is empty!"
                                       % m)
                if m.count("/"):
                    mpath = ("%s/lib/%s" 
                             % (self.user_home.replace("~",
                                                       os.getenv("HOME")),
                                m))
                else:
                    mpath = ("%s/lib/%s/%s" 
                             % (self.user_home.replace("~", 
                                                       os.getenv("HOME")), 
                                username, m))
                f = open(mpath, "w")
                f.write(content)
                f.close()
                self.bc.print_ok("Download module %s successful to '%s'" % 
                                 (m, mpath))
        except HiveServerException, hsx:
            print 'HiveServer: %s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
            if self.debug == 1:
                pdb.set_trace()
        except OSError, oe:
            self.bc.print_err("OSError: %s" % oe)
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_ci(self, line):
        """Commit the user's local lib to server.
        ci [user]
        """
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        l = shlex.split(line)
        if len(l) >= 1:
            username = l[0]
            if username == "":
                username = self.user
        else:
            username = self.user

        # firstly, we scan the list of local lib
        try:
            modules = os.listdir("%s/lib/%s" % 
                                 (self.user_home.replace("~",
                                                         os.getenv("HOME")), 
                                  username))
        except OSError, oe:
            print ("List user %s local lib w/ OSError(%d) '%s'" 
                   % (username, oe.errno, oe.strerror))
            return

        try:
            for m in modules:
                if m == "":
                    continue
                f = open("%s/lib/%s/%s" %
                         (self.user_home.replace("~",
                                                 os.getenv("HOME")),
                          username, m), "r")
                content = f.read()
                if content == "":
                    if m != "__init__.py":
                        self.bc.print_warn("Empty lib file '%s', "
                                           "you should delete it!" 
                                           % m)
                    continue
                # try to upload the file now
                res = self.client.uploadModule(username, m, content)
                if res == "ok":
                    self.bc.print_ok("Upload module %s successful." % m)
        except IOError, ie:
            print "Read module file err(%d) %s" % (ie.errno, ie.strerror)
            return
        except HiveServerException, hsx:
            print '%s' % (hsx.message)
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            print '%s' % (tx.message)
            if self.debug == 1:
                pdb.set_trace()

    def do_cl(self, line):
        """Clean the user's server lib, CAUTION!
        cl [user]
        """
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        l = shlex.split(line)
        if len(l) >= 1:
            username = l[0]
            if username == "":
                username = self.user
        else:
            username = self.user
        try:
            modules = self.client.listModule(username)
            for m in modules:
                if m == "":
                    continue
                content = ""
                # try to upload the file now
                res = self.client.uploadModule(username, m, content)
                if res == "ok":
                    self.bc.print_ok("Clean module %s successful." % m)
        except IOError, ie:
            print "Read module file err(%d) %s" % (ie.errno, ie.strerror)
            return
        except HiveServerException, hsx:
            self.bc.print_err('%s' % (hsx.message))
            if self.debug == 1:
                pdb.set_trace()
        except Thrift.TException, tx:
            print '%s' % (tx.message)
            if self.debug == 1:
                pdb.set_trace()

    def do_lsl(self, line):
        """List the local lib
        lsl [user]
        """
        l = shlex.split(line)
        if len(l) >= 1:
            username = l[0]
            if username == "":
                username = self.user
        else:
            username = self.user
        try:
            modules = os.listdir("%s/lib/%s" % 
                                 (self.user_home.replace("~",
                                                         os.getenv("HOME")), 
                                  username))
        except OSError, oe:
            #print ("List user %s lib w/ OSError(%d) '%s'" 
            #       % (username,
            #          oe.errno, oe.strerror))
            print "TDW-00202 FAILED: OS error (List user %s lib w/ OSError(%d) '%s')" % (username,  oe.errno, oe.strerror)        
            return
        self.bc.print_pink("Local Library for %s:" % username)
        for m in modules:
            if m == "":
                continue
            print m

    def do_reset(self, line):
        """Reset the network stream"""
        if self.client == None:
            print_notconnected()
            return
        if self.session == None or self.authid == None:
            print_notattached()
            return
        try:
            res = self.client.fetchAll()
        except HiveServerException, hsx:
            print 'HiveServer: %s' % (hsx.message)
        except Thrift.TException, tx:
            self.auto_disconnect('Thrift: %s' % (tx.message))
        except:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            traceback.print_exception(exc_type, exc_value, exc_traceback,
                                      limit=2, file=sys.stderr)
            self.do_disconnect("")

    def do_status(self, line):
        """Print the client status"""
        self.bc.print_warn("TDW/PL Client Version 0.2.3")
        self.bc.print_warn("Auto save config: %s" % (self.autosaveconf))
        self.bc.print_warn("Auto save passwd: %s" % (self.savepasswd))
        self.bc.print_warn("User home library: '%s'" % (self.user_home))
        # try to create the library dir now
        try:
            os.makedirs("%s/lib" % (self.user_home.replace("~", 
                                                           os.getenv("HOME"))))
        except OSError:
            # just ignore the error
            pass
        if self.user == None:
            self.bc.print_warn("No username")
            return
        else:
            self.bc.print_warn("User: '%s'" % (self.user))

        self.bc.print_warn("Database name '%s'" % (self.dbname))

        if self.client == None:
            self.bc.print_warn("Server '%s:%s' not connected" % (self.server, 
                                                                 self.port))
            return
        else:
            self.bc.print_warn("Connected to server '%s:%s'" % (self.server, 
                                                                self.port))

        if self.session == None:
            self.bc.print_warn("Not attached to any session.")
            return
        else:
            self.bc.print_warn("Attached to session '%s'" % (self.session))

    def do_color(self, line):
        """Enable/disable the color mode"""
        if self.bc.mode == True:
            print "Disable color mode."
        else:
            print "Enable color mode."
        self.bc.flip()

    def do_autosaveconf(self, line):
        """Toggle auto save config file mode"""
        if self.autosaveconf == True:
            print "Disable auto save config mode"
            self.autosaveconf = False
        else:
            print "Enable auto save config mode"
            self.autosaveconf = True

    def do_savepasswd(self, line):
        """Toggle auto save passwd mode"""
        if self.savepasswd == True:
            print "Disable auto save passwd mode"
            self.savepasswd = False
        else:
            print "Enable auto save passwd mode"
            self.savepasswd = True

    def do_autoed(self, line):
        """Enable/disable the auto edit mode"""
        if self.autoedmode == True:
            print "Disable auto edit mode."
            self.autoedmode = False
        else:
            print "Enable auto edit mode."
            self.autoedmode = True
        
    def auto_disconnect(self, line):
        """Disconnect from the server on error"""
        self.client = None
        self.session = None
        if line == "":
            self.bc.print_err("Unknown error, we disconnect from the server.")
        else:
            self.bc.print_err("%s, We disconnect from the server." % (line))

    def do_server(self, line):
        """Change the default server.
        server server_name"""
        if line == "":
            print self.server
        else:
            if self.client == None:
                self.server = line
                print "You have changed server to '%s'" % (self.server)
            else:
                print "You have connected to server '%s'" % (self.server)

    def do_port(self, line):
        """Change the default server port.
        server server_name"""
        if line == "":
            print self.port
        else:
            if self.client == None:
                self.port = line
                print "You have changed port to '%s'" % (self.port)
            else:
                print "You have connected to server '%s:%s'" % (self.server, 
                                                                self.port)

    def do_dbname(self, line):
        """Change the default database name (Note that: this config
        ONLY impact the put jobs).
        dbname db_name"""
        print "'dbname' is obsolete, please do use it now."
        print "Use 'use database_name' instead."
        return

        if line == "":
            print self.dbname
        else:
            if self.client == None:
                self.dbname = line
                print "You have changed dbname to '%s'" % (self.dbname)
            else:
                print ("You have connected to server '%s' db '%s'" 
                       % (self.server, self.dbname))

    def do_test(self, line):
        """Just for test functions.
        If you do NOT know what does this mean, do NOT call it."""
        x = line
        x = x.rstrip(';')
        print ":" + x + ":"

        if line.endswith('\\'):
            print "True"
        else:
            print "False"
        
        l = line.rsplit("/")
        string = l[len(l) - 1]
        l = string.split(".")
        print l[0]
        print line.rstrip(string)

    def do_sleep(self, line):
        """Sleep for N seconds
        sleep N"""
        if line == "":
            line = 0
        try:
            time.sleep(float(line))
        except ValueError, ve:
            print "TDW-00004 FAILED: TDWClient input arguments error (Invalid sleep argument: %s)" % (ve.strerror)
            #print "Invalid sleep argument: %s" % (ve.strerror)

    def do_EOF(self, line):
        """EOF to exit the shell"""
        if self.client != None:
            self.do_disconnect(line)
        if self.autosaveconf:
            self.post_config()
        return True

    def do_exit(self, line):
        """exit the shell"""
        return self.do_quit(line)
    
    def do_q(self, line):
        """exit the shell"""
        return self.do_quit(line)

    def do_quit(self, line):
        """quit to exit the shell"""
        if self.client != None:
            self.do_disconnect(line)
        if self.autosaveconf:
            self.post_config()
        return True

def tdw_file_checking(fname):
    ''' Do module file checking. It includes import check for os.kill/rmdir,
    module name logging.
    '''
    modules = []
    mode = "server"
    f = open(fname, "r")
    for line in f.readlines():
        # if 'import ' in line:
        #     raise IOError(-1, "'import' is NOT allowed in the TDW/PL code.")
        if '#local' in line:
            mode = "local"
        if line.startswith("import "):
            line = line.replace(",", " ")
            l = shlex.split(line)
            if len(l) >= 2:
                for x in l[1:]:
                    if x != "":
                        x = x.rstrip(" \n\t")
                        modules.append("%s.py" % x)
        if line.startswith("from "):
            try:
                l = shlex.split(line)
                if len(l) >= 4:
                    if l[1] == "tdw_sql_lib" and l[2] == "import":
                        modules.append("global.tdw_sql_lib.py")
            except Exception, e:
                # we can ignore this error
                pass

        if 'os.kill' in line:
            raise IOError(-1, "'os.kill' is NOT allowed in the TDW/PL code.")
        if 'os.rmdir' in line:
            raise IOError(-1, "'os.rmdir' is NOT allowed in the TDW/PL code.")
    f.close()
    return mode, modules

def print_notconnected():
    '''Site is not connected'''
    print "You have not connect to any server."
    print "Use 'status' to get the client state."
    print "Use 'connect/c' to connect the Hive Server."

def print_notattached():
    '''Session is not attached'''
    print "You have not attached to any session."
    print "Use 'status' to get the client state."
    print "Use 'new/n' to new or 'attach/a' to attach a session."
    print "Use 'ls' to view the active sessions."

def print_help():
    '''Top-level help messages'''
    print "Copyright 2009-2010 (c) TDW group of BOSS, Tencent."
    print "TDWPL is a python tools to debug and run your TDW/PL codes."
    print ""
    print "-h, --help                   print this menu."
    print "-a, --action=                specify the action you want to do."
    print (bcolors.OKPINK + "             shell   shell interactive mode." 
           + bcolors.ENDC)
    print (bcolors.OKPINK + "                     You may like this mode :-)" 
           + bcolors.ENDC)
    print (bcolors.OKPINK + "                     If you provide no argument, "
           "we will fall into this mode automatically." + bcolors.ENDC)
    print "             create  just create a new session"
    print "             show    show the current sessions"
    print "             del     delete the specified session."
    print "             get     get the job status of the specified session."
    print "             set     set the job config of the specified session."
    print ("             kill    kill the running job, if there is no job "
           "running,")
    print "                     nothing should be modified."
    print ("             put     upload the job to HiveServer and run it "
           "immediately.")
    print "             debug   run the PL code in debug mode."
    print ""
    print ("-l                           Load the user/passswd configurations "
           "from the config file.")
    print "-f, --file                   give the PL file name."
    print "-s, --session                give the session id."
    print "-u, --authid                 give the session authorized id."
    print "-x, --user                   give the user name."
    print "-y, --passwd                 give the user password."
    print ("-j, --jobid                  give the jobid, default to -1 to be "
           "the last")
    print "-v, --server                 give the server address"
    print "-p, --port                   give the server port"
    print "-d, --dbname                 give the default dbname"
    print "                             jobid of the session."
    print ""
    print ("Any questions please contact 'canma' or 't_BOSSS_TDW' via "
           "email or RTX.")

if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
