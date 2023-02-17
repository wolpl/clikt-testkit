package com.wolpl.clikttestkit

class CliktTestCancellation(override val cause: AssertionError) : Throwable("CliktTest was cancelled!")