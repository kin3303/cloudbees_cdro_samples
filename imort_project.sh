#!/bin/bash -ex

export PATH=$PATH:/opt/cloudbees/sda/bin

ectool login admin changeme

if [ ! -f /tmp/project_import_ready ]; then
  echo "Import Projects"
  sysobjects=("server" "projects" "resources" "artifacts" "repositories" "session" "workspaces")
  
  for file in $PWD/projects/*.xml; do
    ectool import --file "$file" --force 1

    fileName=$(basename "$file")
    projectName=${fileName%.*}
    for sysobject in "${sysobjects[@]}"
    do
       ectool createAclEntry user "project: $projectName" --systemObjectName $sysobject --executePrivilege allow --readPrivilege allow --modifyPrivilege allow --changePermissionsPrivilege allow allow"
    done
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
