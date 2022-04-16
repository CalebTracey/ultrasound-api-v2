package com.ultrasound.app.service;

import com.ultrasound.app.model.data.EType;
import com.ultrasound.app.model.data.ListItem;
import com.ultrasound.app.payload.response.MessageResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface SynchService {
    // Look at the S3 bucket and make any database changes needed
    MessageResponse synchronize(List<String> files);


}
