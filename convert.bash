#!/bin/bash
mongoexport -d $1 -c Post > posts.backup
mongoexport -d $1 -c Page > pages.backup
sed 's/postReference/reference/g' -i posts.backup
sed 's/pageReference/reference/g' -i pages.backup
mongoimport -d $1 -c Post --drop < posts.backup
mongoimport -d $1 -c Page --drop < pages.backup

