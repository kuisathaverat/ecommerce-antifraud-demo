# Licensed to Elasticsearch B.V. under one or more contributor
# license agreements. See the NOTICE file distributed with
# this work for additional information regarding copyright
# ownership. Elasticsearch B.V. licenses this file to you under
# the Apache License, Version 2.0 (the "License"); you may
# not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

VENV ?= .venv
PYTHON ?= python3
PIP ?= pip3
ANSIBLE_OPTS ?=
export VAULT_TOKEN ?= $(shell cat $${HOME}/.vault-token)

export OBJC_DISABLE_INITIALIZE_FORK_SAFETY := YES
export PATH := $(CURDIR)/$(VENV)/bin:$(PATH)
export ANSIBLE_CONFIG := $(CURDIR)/ansible.cfg

export PATH := ${PATH}:${HOME}/bin

SHELL = /bin/bash
MAKEFLAGS += --silent --no-print-directory
.SHELLFLAGS = -ec
.SILENT:


.PHONY: help
help:
	@echo "Environment variables:"
	@echo ""
	@echo "ANSIBLE_OPTS additional flags for Ansible"
	@echo 'VAULT_TOKEN=$$(cat $${HOME}/.vault-token) this token is needed for local operations'
	@echo ""
	@echo "Main targets:"
	@echo ""
	@grep '^## @help' Makefile|cut -d ":" -f 2-3|( (sort|column -s ":" -t) || (sort|tr ":" "\t") || (tr ":" "\t"))

## @help:install-ansible:Install ansible and the dependencies needed in a virtual environment.
.PHONY: install-ansible
install-ansible:
	$(PYTHON) --version;
	test -d $(VENV) || $(PYTHON) -m venv $(VENV);\
	source $(VENV)/bin/activate;\
	$(PYTHON) -m pip install -U pip;\
	$(PIP) install --quiet -r requirements.txt;

.PHONY: deploy
## @help:deploy:Deploy the demo in the k8s cluster.
deploy: 
	$(MAKE) -C otel-ci-demo deploy

.PHONY: deploy-docker
## @help:deploy-docker:Deploy the demo in the k8s cluster. This version uses the tools Docker containet to run the Ansible playbook.
deploy-docker:
	$(MAKE) -C otel-ci-demo deploy-docker

.PHONY: build-tools
## @help:build-tools:Build a Docker container with the tools needed to perform the deployment.
build-tools:
	docker build -t kuisathaverat/tools tools

.PHONY: build-maven
## @help:build-maven:Build a Docker container with the maven and all the dependencies needed to build the app.
build-maven:
	docker build -t kuisathaverat/maven maven

.PHONY: push-maven
## @help:push-maven:Push the maven Docker image to docker.io.
push-maven:
	docker push kuisathaverat/maven

.PHONY: push-tools
## @help:push-tools:Push the tools Docker image to docker.io.
push-tools:
	docker push kuisathaverat/tools


.PHONY: load-gen
load-gen:
	export SMOKE_TEST_URL=otel-ci-app.104.197.53.234.ip.es.io;\
	pytest canary-tests/test_smoke.py::test_healthcheck

clean:
	rm -fr $(VENV) $(CURDIR)/build
