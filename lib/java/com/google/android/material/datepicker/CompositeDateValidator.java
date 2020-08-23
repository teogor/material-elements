/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.material.datepicker;

import static androidx.core.util.Preconditions.checkNotNull;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import java.util.List;

/** A {@link CalendarConstraints.DateValidator} that accepts a list of Date Validators. */
public final class CompositeDateValidator implements CalendarConstraints.DateValidator
{

  @NonNull private final List<CalendarConstraints.DateValidator> validators;

  private CompositeDateValidator(@NonNull List<CalendarConstraints.DateValidator> validators) {
    this.validators = validators;
  }

  /**
   * Returns a {@link CalendarConstraints.DateValidator} that can perform validation for every given validator.
   */
  @NonNull
  public static CalendarConstraints.DateValidator allOf(@NonNull List<CalendarConstraints.DateValidator> validators) {
    return new CompositeDateValidator(validators);
  }

  /** Part of {@link Parcelable} requirements. Do not use. */
  public static final Creator<CompositeDateValidator> CREATOR =
      new Creator<CompositeDateValidator>() {
        @NonNull
        @Override
        public CompositeDateValidator createFromParcel(@NonNull Parcel source) {
          @SuppressWarnings("unchecked")
          List<CalendarConstraints.DateValidator> validators =
              source.readArrayList(CalendarConstraints.DateValidator.class.getClassLoader());
          return new CompositeDateValidator(checkNotNull(validators));
        }

        @NonNull
        @Override
        public CompositeDateValidator[] newArray(int size) {
          return new CompositeDateValidator[size];
        }
      };

  /**
   * Performs the {@link CalendarConstraints.DateValidator#isValid(long)} check as an AND of all validators.
   * e.g. If every validator in this class returns `true` for each {@link
   * CalendarConstraints.DateValidator#isValid(long)}, this this method will return true.
   *
   * @param date milliseconds date to validate against.
   * @return True, if the given date is valid for every given validator in this class.
   */
  @Override
  public boolean isValid(long date) {
    for (CalendarConstraints.DateValidator validator : validators) {
      if (validator == null) {
        continue;
      }
      if (!validator.isValid(date)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(@NonNull Parcel dest, int flags) {
    dest.writeList(validators);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof CompositeDateValidator)) {
      return false;
    }

    CompositeDateValidator that = (CompositeDateValidator) o;

    return validators.equals(that.validators);
  }

  @Override
  public int hashCode() {
    return validators.hashCode();
  }
}
