import clustering.KMeans;

/**
 * Copyright 2016 Filipe Falcão Batista dos Santos
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class Application {

    /**
     * Process:
     *
     * 1. Get the training data for each service.
     * 2. Create the text file containing the clusters centroids and the clusters thresholds.
     * 3. If a service s1 is running:
     *      3.1. Get the data points from s1 and check if the distance from the current point to the s1 centroid is
     *           bigger than the threshold.
     *      3.2. If yes, we have found an anomaly.
     *      3.3. If not, we have found a expected point.
     */
    public static void main(String[] args) {
        KMeans kmeans = new KMeans(true);
        kmeans.init();
        kmeans.calculate();
        kmeans.saveData();
    }

}
