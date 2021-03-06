/*
 * Copyright (C) 2015 Jan "KekS" M.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package de.hsmainz.gi.indoornavcl.positioning;

import org.ejml.simple.SimpleMatrix;

/**
 *
 * Created by Saufaus on 29.01.2015.
 */
public class ParameterEstimation {

    private static final String TAG = ParameterEstimation.class.getSimpleName();


    private final double    EPS             = 1.0e-5;
    private boolean         initialized     = false;
    private boolean         sigma_s_changed = false;
    private double          sigma_s         = 1.0D;     // Standard deviation. Can be changed but in modern computations is not needed anymore
    private double[]        lastPosition    = new double[] {0.0, 0.0, 0.0};
    private double          sigma_0_priori = 1.0D;


    public SimpleMatrix estimateBIBER (SimpleMatrix inputMatrix) {

        // Calculating the DOF (degrees of freedom)
        final int   n               = inputMatrix.numRows();            // # of observations
        final int   u               = 4;                                // # of unknowns (X-,X-,Z-coordinate and scale)
        final int   d               = 3;                                // # of datum defects
        final int   DOF             = n - u + d;
        int iterations = 0;
        double GRZW = 3.5;                                                    //

        // Matrix stuff
        SimpleMatrix L              = new SimpleMatrix(n, 1);           // Observation vector L
        SimpleMatrix l              = new SimpleMatrix(n, 1);
        SimpleMatrix L_0            = new SimpleMatrix(n, 1);
        SimpleMatrix l_old          = new SimpleMatrix(n, 1);
        SimpleMatrix L_0_old        = new SimpleMatrix(n, 1);
        SimpleMatrix sigma_ll       = SimpleMatrix.identity(n);         // Covariance matrix
        SimpleMatrix X_0            = new SimpleMatrix(1, 4);           // Parameter vector
        SimpleMatrix A              = new SimpleMatrix(n, 4);           // Desgin matrix A
        SimpleMatrix N              = new SimpleMatrix(u, u);           // Normal equation
        SimpleMatrix n1             = new SimpleMatrix(u, 1);           // Absolute term
        SimpleMatrix Qdx            = new SimpleMatrix(u, u);
        SimpleMatrix dx             = new SimpleMatrix(u, 1);
        SimpleMatrix v              = new SimpleMatrix(n, 1);           // Vector of
        SimpleMatrix omega          = new SimpleMatrix(1, 1);
        SimpleMatrix sigma_dx       = new SimpleMatrix(u, u);
        SimpleMatrix Q_ll           = sigma_ll.scale(1/(sigma_0_priori * sigma_0_priori)); // VKM of the observations
        SimpleMatrix Q_ll_dach      = new SimpleMatrix(n,n);
        SimpleMatrix Q_vv           = new SimpleMatrix(n,n);
        SimpleMatrix P              = Q_ll.invert();                    // Weight matrix
        SimpleMatrix sigma_ll_post  = new SimpleMatrix(n, n);           // For standard deviations after the processing
        SimpleMatrix deltaL         = new SimpleMatrix(n,1);
        SimpleMatrix R              = new SimpleMatrix(n,n);
        SimpleMatrix output         = new SimpleMatrix(4, 2);
        double checkLoop = 1.0D;
        double sigma_0 = 0.0D;

        // Check if a position is known. If not then we  estimate one here from the measured beacons.
        if (!initialized){
            System.out.println("No initialization found. Calculating position from observations");

            double sumx = 0;
            double sumy = 0;
            //double sumz = 0;

            for (int i=0;i<inputMatrix.numRows();i++){
                sumx += inputMatrix.get(i, 0);
                sumy += inputMatrix.get(i, 1);
            }

            // Update approximate Position
            lastPosition[0] = (sumx/inputMatrix.numRows());
            lastPosition[1] = (sumy/inputMatrix.numRows());
            //lastPosition[2] = sumz;
            lastPosition[2] = 1.5;

            // set status to initialized
            setInitialized(true);
        }

        for (int i = 0; i < n; i++){
            L.set(i,inputMatrix.get(i, 3));
        }

        // Trivial because L_0 is usually filled with zeros
        l = L.minus(L_0);

        // Trivial because we assume sigma_s = 1 (as noted above)
        if (sigma_0_priori != 0) {
            sigma_ll_post.scale(sigma_0_priori * sigma_0_priori);
        }

        // Building X_0 vector : Estimations for X-,Y,-Z-Coordinates and the ppm value
        X_0.set(0, lastPosition[0]);
        X_0.set(1, lastPosition[1]);
        X_0.set(2, lastPosition[2]);
        X_0.set(3, 0.0);

        // Loop for BIBER

        while (checkLoop > EPS) {

            // Building desgin (or jacobi) matrix
            for (int i = 0, rowCount = A.numRows(); i < rowCount; i++) {
                // X
                A.set(i, 0, (
                        -(
                                (
                                        1 + X_0.get(3)
                                ) * (
                                        inputMatrix.get(i, 0) - X_0.get(0)
                                )
                        ) / (
                                Math.sqrt(
                                        Math.pow(inputMatrix.get(i, 0) - X_0.get(0), 2)
                                                + Math.pow(inputMatrix.get(i, 1) - X_0.get(1), 2)
                                                + Math.pow(inputMatrix.get(i, 2) - X_0.get(2), 2)
                                )
                        )
                ));
                // Y
                A.set(i, 1, (
                        -(
                                (
                                        1 + X_0.get(3)
                                ) * (
                                        inputMatrix.get(i, 1) - X_0.get(1)
                                )
                        ) / (
                                Math.sqrt(
                                        Math.pow(inputMatrix.get(i, 0) - X_0.get(0), 2)
                                                + Math.pow(inputMatrix.get(i, 1) - X_0.get(1), 2)
                                                + Math.pow(inputMatrix.get(i, 2) - X_0.get(2), 2)
                                )
                        )
                ));
                // Z
                A.set(i, 2, (
                        -(
                                (
                                        1 + X_0.get(3)
                                ) * (
                                        inputMatrix.get(i, 2) - X_0.get(2)
                                )
                        ) / (
                                Math.sqrt(
                                        Math.pow(inputMatrix.get(i, 0) - X_0.get(0), 2)
                                                + Math.pow(inputMatrix.get(i, 1) - X_0.get(1), 2)
                                                + Math.pow(inputMatrix.get(i, 2) - X_0.get(2), 2)
                                )
                        )
                ));
                // VALUE
                A.set(i, 3,
                        Math.sqrt(
                                Math.pow(inputMatrix.get(i, 0) - X_0.get(0), 2)
                                        + Math.pow(inputMatrix.get(i, 1) - X_0.get(1), 2)
                                        + Math.pow(inputMatrix.get(i, 2) - X_0.get(2), 2)
                        )
                );
            }

            //A.print("%10.4f");

            N = A.transpose().mult(P.mult(A));
            n1 = A.transpose().mult(P.mult(l));

            Qdx = N.pseudoInverse();                                    // N^-1
            dx = Qdx.mult(n1);                                          // x = (N^-1) * n1 | x_dach

            // Update the parameter vector
            X_0 = X_0.plus(dx.transpose());

            // Calculating corrections for the pseudoranges
            v = A.mult(dx).minus(l);

            // Calculate sigma_post
            sigma_0 = Math.sqrt((v.transpose().mult(P.mult(v))).elementSum() / n - u);
            System.out.println("s_o / sigma_0: " + (sigma_0 / sigma_0_priori));
            sigma_dx = Qdx.scale(sigma_0_priori);


            // Update l vectors
            l_old = l.copy();
            l = A.mult(dx);
            L_0_old = L_0.copy();
            L_0 = L_0.plus(l);
            deltaL = l.minus(l_old);



            Q_ll_dach = A.mult(Qdx.mult(A.transpose()));

            Q_vv = Q_ll.minus(Q_ll_dach);
            //Q_vv.print("%10.10f");

            System.out.println("Iteration: " + iterations);
            ///System.out.println("Alte Gewichtsmatrix P:");
            //P.print("%10.5f");

            // BIBER Grenzwert berechnen / Standardisierte Verbesserun
            // Wenn Standardisierte Verbessung > BIBER Gernzwert regewichtung

            R = Q_vv.mult(P);
            for(int i=0; i<Q_vv.numCols();i++){

                if(  ( (Math.abs(v.get(i))/(sigma_0_priori * Math.sqrt(R.get(i,i)))) > (GRZW * Math.sqrt(Q_vv.get(i,i)) ) )  &&  ( (Math.abs(v.get(i))/(sigma_0_priori * Math.sqrt(R.get(i,i)))) < ( (GRZW* -1) *   Math.sqrt(Q_vv.get(i,i))) )   ) {
                    P.set(i, i, 1 / (Math.abs(v.get(i)) / Q_vv.get(i, i)));
                    //System.out.println("Gewichtung angepasst. Element: " + i);
                }
            }

            //System.out.println("Neue Gewichtsmatrix P:");
            //P.print("%10.5f");

            checkLoop = Math.abs(deltaL.elementSum());
            System.out.printf("EPS-Test: " + checkLoop);
            System.out.println();
            System.out.println();
            System.out.println();
            iterations++;
            System.out.println();
            X_0.print("%8.12f");
            System.out.println();
        }

        // Prepare output array
        output.set(0, 0, X_0.get(0));
        output.set(1, 0, X_0.get(1));
        output.set(2, 0, X_0.get(2));
        output.set(3, 0, X_0.get(3));
        output.set(0, 1, Math.sqrt(sigma_dx.get(0, 0)));           // sigma X
        output.set(1, 1, Math.sqrt(sigma_dx.get(1, 1)));           // sigma Y
        output.set(2, 1, Math.sqrt(sigma_dx.get(2, 2)));           // sigma Z
        output.set(3, 1, Math.sqrt(sigma_dx.get(3, 3)));           // sigma scale

        System.out.println();
        sigma_dx.print("%8.15f");
        System.out.println();
        output.print("%10.5f");

        return output;
    }

    /**
     * Estimate the position based on input matrix. If not approximation for the starting position was given, one will be calculated from the input matrix.
     *

     * @param   inputMatrix   a Matrix containing (in order): X-coordinate, Y-coordinate, observed distance to fix point
     * @return  the Location of the Client
     */

    public SimpleMatrix estimate(SimpleMatrix inputMatrix) {

        // Calculating the DOF (degrees of freedom)
        final int   n               = inputMatrix.numRows();            // # of observations
        final int   u               = 4;                                // # of unknowns (X-,X-,Z-coordinate and scale)
        final int   d               = 3;                                // # of datum defects
        final int   DOF             = n - u + d;
        // Matrix stuff
        SimpleMatrix L              = new SimpleMatrix(n, 1);           // Observation vector L
        SimpleMatrix l              = new SimpleMatrix(n, 1);
        SimpleMatrix L_0            = new SimpleMatrix(n, 1);
        SimpleMatrix l_old          = new SimpleMatrix(n, 1);
        SimpleMatrix L_0_old        = new SimpleMatrix(n, 1);
        SimpleMatrix sigma_ll       = new SimpleMatrix(n, n);           // Covariance matrix
        SimpleMatrix X_0            = new SimpleMatrix(1, 4);           // Parameter vector
        SimpleMatrix A              = new SimpleMatrix(n, 4);            // Desgin matrix A
        SimpleMatrix N              = new SimpleMatrix(u, u);           // Normal equation
        SimpleMatrix n1             = new SimpleMatrix(u, 1);           // Absolute term
        SimpleMatrix Qdx            = new SimpleMatrix(u, u);
        SimpleMatrix dx             = new SimpleMatrix(u, 1);
        SimpleMatrix v              = new SimpleMatrix(n, 1);           // Vector of
        SimpleMatrix omega          = new SimpleMatrix(1, 1);
        SimpleMatrix sigma_dx       = new SimpleMatrix(u, u);
        SimpleMatrix Q_ll           = SimpleMatrix.identity(n).scale(Math.pow(sigma_s, 2)); // VKM of the observations
        SimpleMatrix P              = Q_ll.invert();                    // Weight matrix
        SimpleMatrix sigma_ll_post  = new SimpleMatrix(n, n);           // For standard deviations after the processing
        SimpleMatrix output         = new SimpleMatrix(4, 2);

        // Check if a position is known. If not then we  estimate one here from the measured beacons.
        if (!initialized){
            System.out.println("No initialization found. Calculating position from observations");

            double sumx = 0;
            double sumy = 0;
            //double sumz = 0;

            for (int i=0;i<inputMatrix.numRows();i++){
                sumx += inputMatrix.get(i, 0);
                sumy += inputMatrix.get(i, 1);
            }

            // Update approximate Position
            lastPosition[0] = (sumx/inputMatrix.numRows());
            lastPosition[1] = (sumy/inputMatrix.numRows());
            //lastPosition[2] = sumz;
            lastPosition[2] = 2.5;

            // set status to initialized
            setInitialized(true);
        }

        for (int i = 0; i < n; i++){
            L.set(i,inputMatrix.get(i, 3));
        }

        // Trivial because L_0 is usually filled with zeros
        l = L.minus(L_0);

        // Trivial because we assume sigma_s = 1 (as noted above)
        if (sigma_s_changed) {
            sigma_ll_post.scale(sigma_s * sigma_s);
        }

        // Building X_0 vector : Estimations for X-,Y,-Z-Coordinates and the ppm value
        X_0.set(0, lastPosition[0]);
        X_0.set(1, lastPosition[1]);
        X_0.set(2, lastPosition[2]);
        X_0.set(3, 0.0);


        // SChleife ?!

        // Building desgin (or jacobi) matrix
        for (int i = 0, rowCount = A.numRows();i < rowCount; i++){
            // X
            A.set(i, 0, (
                -(
                    (
                        1 + X_0.get(3)
                    ) * (
                        inputMatrix.get(i, 0) - X_0.get(0)
                    )
                )  / (
                    Math.sqrt(
                        Math.pow(inputMatrix.get(i, 0) - X_0.get(0), 2)
                      + Math.pow(inputMatrix.get(i, 1) - X_0.get(1), 2)
                      + Math.pow(inputMatrix.get(i, 2) - X_0.get(2), 2)
                    )
                )
            ));
            // Y
            A.set(i, 1, (
                -(
                    (
                        1 + X_0.get(3)
                    ) * (
                        inputMatrix.get(i, 1) - X_0.get(1)
                    )
                ) / (
                    Math.sqrt(
                        Math.pow(inputMatrix.get(i, 0) - X_0.get(0), 2)
                      + Math.pow(inputMatrix.get(i, 1) - X_0.get(1), 2)
                      + Math.pow(inputMatrix.get(i, 2) - X_0.get(2), 2)
                    )
                )
            ));
            // Z
            A.set(i, 2, (
                -(
                    (
                        1 + X_0.get(3)
                    ) * (
                        inputMatrix.get(i, 2) - X_0.get(2)
                    )
                ) / (
                    Math.sqrt(
                        Math.pow(inputMatrix.get(i, 0) - X_0.get(0), 2)
                      + Math.pow(inputMatrix.get(i, 1) - X_0.get(1), 2)
                      + Math.pow(inputMatrix.get(i, 2) - X_0.get(2), 2)
                    )
                )
            ));
            // VALUE
            A.set(i, 3,
                Math.sqrt(
                    Math.pow(inputMatrix.get(i, 0) - X_0.get(0), 2)
                  + Math.pow(inputMatrix.get(i, 1) - X_0.get(1), 2)
                  + Math.pow(inputMatrix.get(i, 2) - X_0.get(2), 2)
                )
            );
        }

        A.print("%10.4f");

        N = A.transpose().mult(P.mult(A));
        n1 = A.transpose().mult(P.mult(l));

        Qdx = N.pseudoInverse();
        dx = Qdx.mult(n1);

        // Update the parameter vector
        X_0 = X_0.plus(dx.transpose());

        // Calculating corrections for the pseudoranges
        v = A.mult(dx).minus(l);

        omega = v.transpose().mult(P.mult(v));


        sigma_dx = Qdx.scale(sigma_s);


        // Update l vectors
        l_old = l.copy();
        l = A.mult(dx);
        L_0_old = L_0.copy();
        L_0 = L_0.plus(l);


        Q_ll = A.mult(Qdx.mult(A.transpose()));
        sigma_ll_post = Q_ll.scale(sigma_s);

        // Prepare output array
        output.set(0, 0, X_0.get(0));
        output.set(1, 0, X_0.get(1));
        output.set(2, 0, X_0.get(2));
        output.set(3, 0, X_0.get(3));
        output.set(0, 1, Math.sqrt(sigma_dx.get(0, 0)));           // sigma X
        output.set(1, 1, Math.sqrt(sigma_dx.get(1, 1)));           // sigma Y
        output.set(2, 1, Math.sqrt(sigma_dx.get(2, 2)));           // sigma Z
        output.set(3, 1, Math.sqrt(sigma_dx.get(3, 3)));           // sigma scale

        System.out.println();
        sigma_dx.print("%8.6f");
        System.out.println();
        output.print("%10.5f");

        return output;
    }

    public double[] getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(double[] lastPosition) {
        this.lastPosition = lastPosition;
        setInitialized(true);
        System.out.println("----- Position initialized -----");
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public double getSigma_s() {
        return sigma_s;
    }

    public void setSigma_s(double sigma_s) {
        this.sigma_s = sigma_s;
        sigma_s_changed = true;
    }

}