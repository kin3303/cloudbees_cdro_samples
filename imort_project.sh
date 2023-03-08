#!/bin/bash -ex

while getopts s: flag
do
    case "${flag}" in 
      s) server=${OPTARG};; 
    esac
done
 
if [ -z "$server" ]; then
    echo '[Error] Please put a server host information.'
    exit 1 
fi

export PATH=$PATH:/opt/cloudbees/sda/bin 

ectool --server $server login admin changeme

echo "Create Sample Resources"

if [ ! -f $PWD/demo_prod_agents_ready ]; then
  ectool createResource PROD --hostName localhost
  ectool pingResource PROD
  touch $PWD/demo_prod_agents_ready
fi

if [ ! -f $PWD/demo_dev_agents_ready ]; then
  ectool createResource DEV --hostName  localhost 
  ectool pingResource DEV
  touch $PWD/demo_dev_agents_ready
fi

if [ ! -f $PWD/demo_qa_agents_ready ]; then
  ectool createResource QA --hostName  localhost 
  ectool pingResource QA
  touch $PWD/demo_qa_agents_ready
fi

echo "Import Projects"
  
if [ ! -f $PWD/project_import_ready ]; then

  for file in $PWD/projects/*.xml; do
    ectool import --file "$file" --force 1

    fileName=$(basename "$file")
    projectName=${fileName%.*}

    ectool createAclEntry user "project: $projectName" --systemObjectName server --executePrivilege allow --readPrivilege allow --modifyPrivilege allow --changePermissionsPrivilege allow
    ectool createAclEntry user "project: $projectName" --systemObjectName projects --executePrivilege allow --readPrivilege allow --modifyPrivilege allow --changePermissionsPrivilege allow
    ectool createAclEntry user "project: $projectName" --systemObjectName resources --executePrivilege allow --readPrivilege allow --modifyPrivilege allow --changePermissionsPrivilege allow
    ectool createAclEntry user "project: $projectName" --systemObjectName artifacts --executePrivilege allow --readPrivilege allow --modifyPrivilege allow --changePermissionsPrivilege allow
    ectool createAclEntry user "project: $projectName" --systemObjectName repositories --executePrivilege allow --readPrivilege allow --modifyPrivilege allow --changePermissionsPrivilege allow
    ectool createAclEntry user "project: $projectName" --systemObjectName session --executePrivilege allow --readPrivilege allow --modifyPrivilege allow --changePermissionsPrivilege allow
    ectool createAclEntry user "project: $projectName" --systemObjectName workspaces --executePrivilege allow --readPrivilege allow --modifyPrivilege allow --changePermissionsPrivilege allow
  done

  touch $PWD/project_import_ready
fi

echo "Import DSL files"
 
if [ ! -f $PWD/dsl_import_ready ]; then
  for file in $PWD/projects/*.groovy; do
    ectool evalDsl --dslFile "$file"
  done

  touch $PWD/dsl_import_ready
fi
