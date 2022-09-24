FROM python:3.9

WORKDIR /tools
RUN pip install --upgrade pip
COPY ansible/* .
RUN python -m pip install -r requirements.txt
ENV PATH="${PATH}:/tools:/tools/bin:/tools/bin/google-cloud-sdk/bin"
RUN ansible-playbook playbook-tools.yml
