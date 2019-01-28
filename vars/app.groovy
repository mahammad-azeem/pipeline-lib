def call() {

try {

node() {

stage('checkoutSCM') {
checkout(
[$class:'checkoutSubversionSCM', remote:'svn/url/to/be/checkout', location:'/d/Softwares', credentialsID: 'Azeem']
)
}

stage('build') {
sh """
gradle -b /d/Softwares/gradle/build.gradle
"""
}

stage('junit') {
junit '/d/Softwares/*.xml'
}
stage('notifyEmail') {
timeout(time: 2, unit: 'HOURS')(
input(message: 'please click here if the junit coverage looks good', submitter: 'dev.test@techm.com, dev.junit.team@techm.com')
emailext(
subject:'Notify email to dev for junit coverage',
body:"""<p>Please follow the job url "<a href=www.junitcoverage.com/$(env.JOB_NAME)>Click here to see junit coverage</a>"</p>
     <p> To figure out the junit test case coverage / reports </p>""",
to:'devops@techm.com, devlead@techm.com'
)
)
}
stage('deploytodev'){
sh """
echo -e "deploying $(env.JOB_NAME) TO DEV" | mailx -s "DEV deployment via Pipeline" dev@techm.com devops@techm.com
ansible -i /path/to/inventory/hosts dev --private-key-file=/path/to/the/keyfile -m copy -a "src=/artifact/path/ofthe/builtfile.jar dest=/path/to/deployon/remotemachine"
"""
}
stage('QAapproval') {
timeout(time: 1, unit: 'DAYS')(
input(message: 'please click here if the junit coverage looks good', submitter: 'QA.test@techm.com, QA.team@techm.com')
emailext(
subject:'Notify email to dev for junit coverage',
body:"""<p>Please follow the job url "<a href=www.junitcoverage.com/$(env.JOB_NAME)>Click here to see junit coverage</a>"</p>
     <p> To figure out the junit test case coverage / reports </p>""",
to:'devops@techm.com, QAlead@techm.com'
)
)
}
stage('deploytoQA'){
sh """
echo -e "deploying $(env.JOB_NAME) TO QA" | mailx -s "QA deployment via Pipeline" dev@techm.com devops@techm.com
ansible -i /path/to/inventory/hosts QA --private-key-file=/path/to/the/keyfile -m copy -a "src=/artifact/path/ofthe/builtfile.jar dest=/path/to/deployon/remotemachine"
"""
}
}
}


catch(err) {
println "Getting error"
println "Please check the Jenkins console output to see the exact reason/error for failure"
}
