build:
	mkdir -p build
	ant create_arr_jar

clean:
	rm -r build
	ant clean

deploy-assets:
	mput -f ./jobs/generate_arrivals.sh /bpijewski/stor/munistats/assets/generate_arrivals.sh
	mput -f ./jobs/generate_trips.sh /bpijewski/stor/munistats/assets/generate_trips.sh
	mput -f ./build/arrival_predictor.jar /bpijewski/stor/munistats/assets/arrival_predictor.jar
	mput -f ./build/trips.jar /bpijewski/stor/munistats/assets/trips.jar

deploy: build deploy-assets
