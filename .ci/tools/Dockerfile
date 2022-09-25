FROM python:3.9

WORKDIR /home/jenkins
RUN useradd -m -d /home/jenkins -s /bin/bash jenkins
RUN chown -R jenkins:jenkins /home/jenkins
WORKDIR /tools
RUN pip install --upgrade pip
COPY ansible/* .
RUN python -m pip install -r requirements.txt
ENV PATH="${PATH}:/tools:/tools/bin:/tools/bin/google-cloud-sdk/bin"
RUN ansible-playbook playbook-tools.yml
RUN chown -R jenkins:jenkins /home/jenkins
RUN chown -R jenkins:jenkins /tools
USER jenkins
