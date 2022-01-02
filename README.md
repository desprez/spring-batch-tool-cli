# spring-batch-tool-cli

CLI Tools to manage spring batch jobs.


The general form of execution is:
java -Dspring.datasource.url= -jar batch-toolbox.jar COMMAND SUBCOMMAND

To get further help for the desired COMMAND or SUBCOMMAND use the HELP command
as follows:

1. java -D... -jar ... help COMMAND
2. java -D... -jar ... help COMMAND SUBCOMMAND

1. will list available subcommands for the given command.
2. will show available parameters for the given subcommand.

_Example_ :

List all jobs, using H2 database

```
java -D'spring.datasource.url=jdbc:h2:~/mts;AUTO_SERVER=TRUE' -jar batch-toolbox.jar jobs list
```

**Commands**:
-  help          Displays help information about the specified command
-  jobs          Manage executed jobs.
-  executions    Manage single execution instances.
-  purgehistory  Delete data from Spring Batch Metadata tables that are N months old.


## Jobs SubCommand

**Usage**:  jobs SUBCOMMAND

Manage executed jobs.

**Commands**:
-  help     Displays help information about the specified command
-  list     List the name of all executed jobs.
-  stop     Stop all running executions for the job.
-  abandon  Abandon all stopped executions for the job.

### List

**Usage**:  jobs list
List the name of all executed jobs.

_Example_ :

List all executed jobs

```
java -jar batch-toolbox.jar jobs list

complex-job
daily-job
jobTest
purgehistoryjob
simple-export-job
table2filesynchro-job
```

### Stop

**Usage**:  jobs stop jobName

Stop all running executions for the job.

   _jobName_   The name of the job, which should stopped.

### Abandon

**Usage**:  jobs abandon jobName

Abandon all stopped executions for the job.

   _jobName_   The name of the job, which should abandoned.


## Executions SubCommand

**Usage**:  executions SUBCOMMAND

Manage single execution instances.

This is an advanced feature, normally managing your job via 'jobs' command should be sufficient.
Enter 'executions help SUBCOMMAND' to find out parameters for the specified subcommand.

**Commands**:
-  help    Displays help information about the specified command
-  list    List all executions for the job.
-  status  Set the status of the given execution.

_Example_ :

List execution of the simple-export-job

```
java -jar batch-toolbox.jar executions list simple-export-job --printExit --printParams
ID      Start Time      End Time        Status  Parameters      Exit status
7       2021-12-23 16:02:26.252 2021-12-23 16:02:26.694 COMPLETED       {output-dir=target\output, run.id=1}    exitCode=COMPLETED;exitDescription=
```

Change Status of the execution #7 to COMPLETED

```
java -jar batch-toolbox.jar executions status 7 COMPLETED

Done.
```

## Purgehistory SubCommand

**Usage**:  purgehistory [--months=<historyRetentionMonth>]

Delete data from Spring Batch Metadata tables that are N months old.

  --months=<historyRetentionMonth> the metadatas retention (in months)