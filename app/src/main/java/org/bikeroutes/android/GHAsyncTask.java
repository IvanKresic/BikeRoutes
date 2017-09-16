package org.bikeroutes.android;

import android.os.AsyncTask;

public abstract class GHAsyncTask<A, B, C> extends AsyncTask<A, B, C>
{
    private Throwable error;

    protected abstract C saveDoInBackground( A... params ) throws Exception;

    protected C doInBackground( A... params )
    {
        try
        {
            return saveDoInBackground(params);
        } catch (Throwable t)
        {
            error = t;
            return null;
        }
    }

    protected boolean hasError()
    {
        return error != null;
    }

    protected Throwable getError()
    {
        return error;
    }

    protected String getErrorMessage()
    {
        if (hasError())
        {
            return error.getMessage();
        }
        return "No Error";
    }
}
