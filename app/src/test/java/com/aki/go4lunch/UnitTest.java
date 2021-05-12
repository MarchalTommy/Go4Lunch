package com.aki.go4lunch;

import android.app.Instrumentation;
import android.content.pm.InstrumentationInfo;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Test
    public void connection() {
    }
}