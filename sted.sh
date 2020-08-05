#!/bin/sh
# ---------------------------------------------------------------------
# Normally, editing this script should not be required.
# Only case is to set up JAVA_HOME if it's not already defined.
#
# To specify an alternative JVM, edit and uncomment the following
# line and change the path accordingly.
# ---------------------------------------------------------------------
# Set the JAVA Installation Directory
# Uncomment and set JAVA_HOME below to point to jdk if script fails to locate jdk
# ---------------------------------------------------------------------
JAVA_HOME=/opt/tools/jdk-14.02

JAVA_EXE="java"
if [ -n $JAVA_HOME ] ; then
    _TMP="$JAVA_HOME/bin/java"
    if [ -f "$_TMP" ] ; then
        if [ -x "$_TMP" ] ; then
            JAVA_EXE="$_TMP"
        else
            echo "Warning: $_TMP is not executable"
        fi
    else
        echo "Warning: $_TMP does not exist"
    fi
fi
if ! which "$JAVA_EXE" >/dev/null ; then
    echo "Error: No Java Runtime Environment found"
    echo "Please set the environment variable JAVA_HOME to the root directory of your SUN Java installation, e.g. by editing the 7th line in this launcher script."
    exit 1
fi

#
# Resolve the location of the JStock installation.
# This includes resolving any symlinks.
PRG=$0
while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '^.*-> \(.*\)$' 2>/dev/null`
    if expr "$link" : '^/' 2> /dev/null >/dev/null; then
        PRG="$link"
    else
        PRG="`dirname "$PRG"`/$link"
    fi
done

# Before you run STED specify the location of the directory where STED is installed
# In most cases you do not need to change the settings below.
# ---------------------------------------------------------------------
# ---------------------------------------------------------------------
# Ensure STED_HOME points to the directory where the STED is installed.
# ---------------------------------------------------------------------
STED_HOME=`dirname "$PRG"`
STED_BIN_HOME=$PRG

#echo ---------------------------------------------------------------------
#echo Please make sure 'sted.jar' & 'sted-widgets.jar' is in the 'bin' folder.
#echo ---------------------------------------------------------------------
#CLASS_PATH="$STED_HOME/sted.jar:$STED_HOME/sted-widgets.jar:$STED_HOME"

# ---------------------------------------------------------------------
# Set STED path to customize
# ---------------------------------------------------------------------
# path to user settings
STED_SETTINGS_PATH=$STED_HOME/settings
# path to images
STED_ICON_PATH=$STED_HOME/images
# path to resource
STED_RESOURCE_PATH=$STED_HOME/resource
# path to store log
STED_LOG_PATH=$STED_HOME/log

# ---------------------------------------------------------------------
# IF RUNNING CONSOLE.. SET CONSOLE DEPENDENT SYSTEM PROPERTIES
# ---------------------------------------------------------------------
STED_CONSOLE_JVM_ARGS="-Dfontmap.file=fontmap.xml -Dinput.file=input.txt -Doutput.file=output.txt"

# ---------------------------------------------------------------------
# You may specify your own JVM arguments in STED_JVM_ARGS variable.
# ---------------------------------------------------------------------
STED_JVM_ARGS="-Xms128m -Xmx512m"
#STED_JVM_ARGS=

# ---------------------------------------------------------------------
# NO NEED TO CHANGE ANYTHING BELOW
# ---------------------------------------------------------------------
#ALL_JVM_ARGS="$STED_JVM_ARGS -Dsted.home.path=$STED_HOME -Dsted.settings.path=$STED_SETTINGS_PATH -Dsted.icon.path=$STED_ICON_PATH -Dsted.resource.path=$STED_RESOURCE_PATH -Dsted.log.path=$STED_LOG_PATH"
ALL_JVM_ARGS=$STED_JVM_ARGS

OLD_PATH=$PATH
PATH=$STED_HOME:$PATH

# ---------------------------------------------------------------------
# STED Main Class
# ---------------------------------------------------------------------
#MAIN_CLASS_NAME=intellibitz.sted.Main

# ---------------------------------------------------------------------
# Launch STED Console
# ---------------------------------------------------------------------
#ALL_JVM_ARGS="$STED_CONSOLE_JVM_ARGS $ALL_JVM_ARGS"
#"$JAVA_EXE" $JVM_ARGS -cp "$CLASS_PATH" $MAIN_CLASS_NAME -c $*

OLD_DIR=`pwd`
cd $STED_HOME
# ---------------------------------------------------------------------
# Launch STED GUI
# ---------------------------------------------------------------------
#"$JAVA_EXE" $ALL_JVM_ARGS -cp "$CLASS_PATH" $MAIN_CLASS_NAME $*
echo $JAVA_EXE $ALL_JVM_ARGS -jar sted.jar $*
$JAVA_EXE $ALL_JVM_ARGS -jar sted.jar $*

PATH=$OLD_PATH
cd $OLD_DIR
############################################################
