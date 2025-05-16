init-stripe:
	stripe listen --forward-to http://localhost:8080/webhook

# se: stripe-event
# sess-complete commannd will create a session with subsecription id set to null therefore will throw an error
userId ?= 7
orgId ?= 28
# the customerId exists in stripe was created for testing (stripe test mode)
customerId ?= 
sess-complete:
	@if [ -z "$(customerId)" ]; then \
		echo "customerId is required pass it like: make se-sess-complete customerId=..."; \
		exit 1; \
	fi
	stripe trigger checkout.session.completed \
  	--add checkout_session:metadata.user_id=$(userId) \
  	--add checkout_session:metadata.organization_id=$(orgId) \
  	--add checkout_session:customer=$(customerId) \
	--add checkout_session:mode=subscription

# this customer exists in our database with email and name
create-testing-customer:
	stripe customers create --email user10@gmail.com --name "john doe"

test:
	mvn test -Dspring.profiles.active=test

# this for local running github actions via cli tool "act-cli"
# act --secret-file .env --pull=false
test-ci:
	act --secret-file .env --pull=false