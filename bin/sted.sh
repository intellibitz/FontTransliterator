#!/bin/sh
#<!--
# #
 #  $Id:sted.sh 55 2007-05-19 05:55:34Z sushmu $
 #  $HeadURL: svn+ssh://sushmu@svn.code.sf.net/p/sted/code/FontTransliterator/trunk/bin/sted.sh $
 # /
#-->
############################################################

# ---------------------------------------------------------------------
# Set the JAVA Installation Directory
# Uncomment and set JAVA_HOME below to point to jdk if script fails to locate jdk
# ---------------------------------------------------------------------
#JAVA_HOME=/opt/tools/jdk1.7.0_10

# ---------------------------------------------------------------------
# Before you run STED specify the location of the directory where STED is installed
# In most cases you do not need to change the settings below.
# ---------------------------------------------------------------------
# ---------------------------------------------------------------------
# Ensure STED_HOME points to the directory where the STED is installed.
# ---------------------------------------------------------------------
SCRIPT_LOCATION=$0
READLINK=`which readlink`
if [ -x "$READLINK" ]; then
  while [ -L "$SCRIPT_LOCATION" ]; do
    SCRIPT_LOCATION=`"$READLINK" -e "$SCRIPT_LOCATION"`
  done
fi

STED_HOME=`dirname "$SCRIPT_LOCATION"`/..
STED_BIN_HOME=`dirname "$SCRIPT_LOCATION"`

# ---------------------------------------------------------------------
# Locate a JDK installation directory which will be used to run the IDE.
# Try (in order): IDEA_JDK, JDK_HOME, JAVA_HOME, "java" in PATH.
# ---------------------------------------------------------------------
if [ -n "$IDEA_JDK" -a -x "$IDEA_JDK/bin/java" ]; then
  JDK="$IDEA_JDK"
elif [ -n "$JDK_HOME" -a -x "$JDK_HOME/bin/java" ]; then
  JDK="$JDK_HOME"
elif [ -n "$JAVA_HOME" -a -x "$JAVA_HOME/bin/java" ]; then
  JDK="$JAVA_HOME"
else
  JAVA_BIN_PATH=`which java`
  if [ -n "$JAVA_BIN_PATH" ]; then
    if [ -z "$JDK" -a -x "$READLINK" ]; then
      JAVA_LOCATION=`"$READLINK" -f "$JAVA_BIN_PATH"`
      case "$JAVA_LOCATION" in
        */jre/bin/java)
          JAVA_LOCATION=`echo "$JAVA_LOCATION" | xargs dirname | xargs dirname | xargs dirname` ;;
        *)
          JAVA_LOCATION=`echo "$JAVA_LOCATION" | xargs dirname | xargs dirname` ;;
      esac
      if [ -x "$JAVA_LOCATION/bin/java" ]; then
        JDK="$JAVA_LOCATION"
      fi
    fi
  fi
fi

if [ -z "$JDK" ]; then
  echo
  echo "No JDK found. Setting java executable to java"
  echo
	echo ---------------------------------------------------------------------
	echo ERROR: JAVA_HOME invalid. Will use 'java' instead of the full path
	echo NOTE:  Please set JAVA_HOME in 'sted.sh' if 'java' not in path.
	echo ---------------------------------------------------------------------
	JAVA_EXE=`which java`
else
    # ---------------------------------------------------------------------
    # JAVA executable.. set from JDK
    # ---------------------------------------------------------------------
    JAVA_EXE=$JDK/bin/java
fi

#echo $JAVA_EXE
eval $JAVA_EXE &> /dev/null
#echo $?
if [ $? -eq 0 ] ; then
    echo "$JAVA_EXE is valid"
else
  echo "ERROR: cannot start STED."
  echo "No JDK found. Please validate either IDEA_JDK, JDK_HOME or JAVA_HOME environment variable points to valid JDK installation."
  echo
  echo "Press Enter to continue."
  read IGNORE
  exit 1
fi

#echo ---------------------------------------------------------------------
#echo Please make sure 'sted.jar' & 'sted-widgets.jar' is in the 'bin' folder.
#echo ---------------------------------------------------------------------
CLASS_PATH="$STED_HOME/bin/sted.jar:$STED_HOME/bin/sted-widgets.jar:$STED_HOME"

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
#STED_JVM_ARGS="-Xms128m"
STED_JVM_ARGS=

# ---------------------------------------------------------------------
# NO NEED TO CHANGE ANYTHING BELOW
# ---------------------------------------------------------------------
ALL_JVM_ARGS="$STED_JVM_ARGS -Dsted.home.path=$STED_HOME -Dsted.settings.path=$STED_SETTINGS_PATH -Dsted.icon.path=$STED_ICON_PATH -Dsted.resource.path=$STED_RESOURCE_PATH -Dsted.log.path=$STED_LOG_PATH"

OLD_PATH=$PATH
PATH=$STED_HOME/bin:$PATH

# ---------------------------------------------------------------------
# STED Main Class
# ---------------------------------------------------------------------
MAIN_CLASS_NAME=intellibitz.sted.Main

# ---------------------------------------------------------------------
# Launch STED Console
# ---------------------------------------------------------------------
#ALL_JVM_ARGS="$STED_CONSOLE_JVM_ARGS $ALL_JVM_ARGS"
#"$JAVA_EXE" $JVM_ARGS -cp "$CLASS_PATH" $MAIN_CLASS_NAME -c $*

# ---------------------------------------------------------------------
# Launch STED GUI
# ---------------------------------------------------------------------
#"$JAVA_EXE" $ALL_JVM_ARGS -cp "$CLASS_PATH" $MAIN_CLASS_NAME $*
  echo "$JAVA_EXE" "$ALL_JVM_ARGS" -cp "$CLASS_PATH" $MAIN_CLASS_NAME $*
  eval "$JAVA_EXE" "$ALL_JVM_ARGS" -cp "$CLASS_PATH" $MAIN_CLASS_NAME $*
# ---------------------------------------------------------------------
# Run STED.
# ---------------------------------------------------------------------
#while true ; do
#  echo "$JAVA_EXE" "$ALL_JVM_ARGS" -cp "$CLASS_PATH" -Djb.restart.code=88 $MAIN_CLASS_NAME $*
#  eval "$JAVA_EXE" "$ALL_JVM_ARGS" -Djb.restart.code=88 -cp "$CLASS_PATH" $MAIN_CLASS_NAME $*
# "$JAVA_EXE" "$ALL_JVM_ARGS" -Djb.restart.code=88 -cp "$CLASS_PATH" $MAIN_CLASS_NAME $*
#  test $? -ne 88 && break
#done

PATH=$OLD_PATH
############################################################
# <!--
# /# #
 #  Copyright (C) IntelliBitz Technologies.,  Muthu Ramadoss
 #  168, Medavakkam Main Road, Madipakkam, Chennai 600091, Tamilnadu, India.
 #  http://www.intellibitz.com
 #  training@intellibitz.com
 #  +91 44 2247 5106
 # http://groups.google.com/group/etoe
 # http://sted.sourceforge.net
 #
 #  This program is free software; you can redistribute it and/or
 #  modify it under the terms of the GNU General Public License
 #  as published by the Free Software Foundation; either version 2
 #  of the License, or (at your option) any later version.
 #
 #  This program is distributed in the hope that it will be useful,
 #  but WITHOUT ANY WARRANTY; without even the implied warranty of
 #  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 #  GNU General Public License for more details.
 #
 #  You should have received a copy of the GNU General Public License
 #  along with this program; if not, write to the Free Software
 #  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 #
 #  STED, Copyright (C) 2007 IntelliBitz Technologies
 #  STED comes with ABSOLUTELY NO WARRANTY;
 #  This is free software, and you are welcome
 #  to redistribute it under the GNU GPL conditions;
 #
 #  Visit http://www.gnu.org/ for GPL License terms.
 # /
#-->

