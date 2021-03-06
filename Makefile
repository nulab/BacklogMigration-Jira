.DEFAULT_GOAL := help

build:  ## Build a jar file
	@sbt clean "testOnly com.nulabinc.*" assembly

testwatch: ## Unit test
	sbt "~testOnly com.nulabinc.*"

.PHONY: help
help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
