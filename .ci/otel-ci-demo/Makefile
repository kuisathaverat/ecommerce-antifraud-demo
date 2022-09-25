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

.PHONY: install-callback
## @help:install-callback:Install the Ansible Opentelemetry callback plugin.
install-callback:
	mkdir -p callback
	curl -sS -o callback_plugins/opentelemetry.py https://raw.githubusercontent.com/ansible-collections/community.general/496bf27b5c896c2b8f055e04692e1c5db2288584/plugins/callback/opentelemetry.py

.PHONY: deploy
## @help:deploy:Deploy the demo in the k8s cluster.
deploy: install-ansible
	source $(VENV)/bin/activate;\
	ansible --version;\
	ansible-playbook playbook.yml

deploy-docker:
	docker run \
		-it \
		-v $(CURDIR):/app \
		-w /app \
		--rm \
		-e VAULT_TOKEN \
		-e VAULT_ADDR \
		-e VAULT_ROLE_ID \
		-e VAULT_SECRET_ID \
		-e VAULT_AUTH_METHOD \
		-e VAULT_AUTHTYPE \
		-e OTEL_EXPORTER_OTLP_INSECURE \
		-e OTEL_EXPORTER_OTLP_ENDPOINT \
		-e APP_VERSION \
		kuisathaverat/tools ansible-playbook $(ANSIBLE_OPTS) playbook.yml

clean:
	rm -fr $(VENV) $(CURDIR)/build
