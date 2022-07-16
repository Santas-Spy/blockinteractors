package santas.spy.config;

import java.util.ArrayList;
import java.util.List;

public class ErrorLogger 
{
    List<Error> errors = new ArrayList<>();
    
    public void add(String error, String line)
    {
        errors.add(new Error(error, line));
    }

    public String list()
    {
        StringBuilder errorMessage = new StringBuilder();
        for (Error error : errors) {
            errorMessage.append(String.format("Error at section \"%s\". %s\n", error.line, error.desc));
        }

        return errorMessage.toString();
    }

    private class Error
    {
        private String desc;
        private String line;

        public Error(String desc, String line)
        {
            this.desc = desc;
            this.line = line;
        }
    }
}
