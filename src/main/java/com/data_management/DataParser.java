package com.data_management;

import java.util.Scanner;

public abstract class DataParser implements DataReader{
    public void readData(DataStorage dataStorage){
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();

        if(userInput.equals("")){

        }

    }
}
