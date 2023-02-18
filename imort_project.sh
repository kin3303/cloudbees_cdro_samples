#!/bin/bash -ex

export PATH=$PATH:/opt/cloudbees/sda/bin

ectool login admin changeme

if [ ! -f /tmp/project_import_ready ]; then
  echo "Import Projects"
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

  touch /tmp/project_import_ready
fi

if [ ! -f /tmp/dsl_import_ready ]; then
  echo "Import DSL files"
  for file in $PWD/projects/*.groovy; do
    ectool evalDsl --dslFile "$file"
  done

  touch /tmp/dsl_import_ready
fi
